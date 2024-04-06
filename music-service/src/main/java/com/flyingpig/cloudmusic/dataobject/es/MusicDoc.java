package com.flyingpig.cloudmusic.dataobject.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.search.SearchHit;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MusicDoc {
    private Long id;
    private String name;
    private String introduce;
    private String singerName;

    public static MusicDoc fromSearchHit(SearchHit hit) {
        String id = hit.getId();
        String name = (String) hit.getSourceAsMap().get("name");
        String introduce = (String) hit.getSourceAsMap().get("introduce");
        String singerName = (String) hit.getSourceAsMap().get("singerName");

        return new MusicDoc(Long.parseLong(id), name, introduce, singerName);
    }

}
