package com.flyingpig.cloudmusic.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;


public class AliOSSUtils {
    private static String endpoint = "";
    private static String bucketName = "";
    private static String accessKeyId = "";
    private static String accessKeySecret = "";


    /**
     * 实现上传图片到OSS
     */
    public String upload(MultipartFile multipartFile) throws IOException {
        // 获取上传的文件的输入流
        InputStream inputStream = multipartFile.getInputStream();

        // 生成新的文件名
        String fileName = generateFileName(multipartFile);

        // 上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        ossClient.putObject(bucketName, fileName, inputStream);

        // 文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;

        // 关闭ossClient
        ossClient.shutdown();
        return url; // 把上传到oss的路径返回
    }

    /**
     * 生成文件名：原始文件名 + 毫秒级时间戳 + UUID前 6 位 + 后缀
     */
    private String generateFileName(MultipartFile multipartFile) {
        // 获取原始文件名和后缀
        String originalFilename = multipartFile.getOriginalFilename();
        String fileExtension = null;
        if (originalFilename != null) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 获取当前时间戳并格式化到毫秒
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = dateFormat.format(new Date());

        // 生成 UUID 并取前 6 位
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 6); // 取前 6 位

        // 组合新的文件名
        return originalFilename.substring(0, originalFilename.lastIndexOf("."))
                + "_" + timestamp
                + "_" + uuid
                + fileExtension;
    }


    /**
     * 初始化分片上传
     */
    public String initMultipartUpload(String uploadId, int partNumber, InputStream inputStream, String originalFilename) {
        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        // 创建InitiateMultipartUploadRequest对象。
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, fileName);

        // 初始化分片。
        InitiateMultipartUploadResult upresult = ossClient.initiateMultipartUpload(request);

        // 关闭ossClient
        ossClient.shutdown();

        return upresult.getUploadId();
    }

    /**
     * 上传分片
     */
    public PartETag uploadPart(String uploadId, int partNumber, String filename, MultipartFile multipartFile, long startPos, long curPartSize, int i, String originalFilename) throws IOException {
        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建UploadPartRequest对象。
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setKey(filename);
        uploadPartRequest.setUploadId(uploadId);
        // 设置上传的分片流。
        // 以本地文件为例说明如何创建FIleInputstream，并通过InputStream.skip()方法跳过指定数据。
        InputStream instream = multipartFile.getInputStream();
        instream.skip(startPos);
        uploadPartRequest.setInputStream(instream);
        // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
        uploadPartRequest.setPartSize(curPartSize);
        // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
        uploadPartRequest.setPartNumber(i + 1);
        // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
        UploadPartResult uploadPartResult = ossClient.uploadPart(uploadPartRequest);

        // 关闭ossClient
        ossClient.shutdown();

        // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
        return uploadPartResult.getPartETag();


    }


    /**
     * 完成分片上传
     */
    public String completeMultipartUpload(String originalFilename, String uploadId, List<PartETag> partETags) {
        //上传文件到 OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 创建CompleteMultipartUploadRequest对象。
        // 在执行完成分片上传操作时，需要提供所有有效的partETags。OSS收到提交的partETags后，会逐一验证每个分片的有效性。当所有的数据分片验证通过后，OSS将把这些分片组合成一个完整的文件。
        String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, fileName, uploadId, partETags);
        // 完成分片上传。
        CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);

        //文件访问路径
        String url = endpoint.split("//")[0] + "//" + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;

        // 关闭ossClient
        ossClient.shutdown();
        return url;// 把上传到oss的路径返回
    }

    /**
     * 根据文件完整 URL 删除 OSS 上的文件
     *
     * @param fileUrl 文件的完整 URL
     * @return 是否删除成功
     */
    public boolean deleteFileByUrl(String fileUrl) {
        // 检查 URL 是否包含正确的 OSS 格式
        if (!fileUrl.contains(bucketName) || !fileUrl.contains(endpoint)) {
            System.out.println("URL 格式错误，不属于当前 OSS 存储桶");
            return false;
        }

        // 从 URL 中提取文件路径和文件名
        String fileKey = fileUrl.substring(fileUrl.indexOf(bucketName) + bucketName.length() + 1);

        // 创建 OSS 客户端实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 删除指定文件
            ossClient.deleteObject(bucketName, fileKey);
            return true;
        } catch (Exception e) {
            System.out.println("删除文件失败: " + e.getMessage());
            return false;
        } finally {
            // 关闭 OSS 客户端
            ossClient.shutdown();
        }
    }

}
