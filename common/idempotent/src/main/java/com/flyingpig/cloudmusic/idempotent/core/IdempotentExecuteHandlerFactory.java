/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flyingpig.cloudmusic.idempotent.core;


import com.flyingpig.cloudmusic.idempotent.core.bases.ApplicationContextHolder;
import com.flyingpig.cloudmusic.idempotent.core.param.IdempotentParamService;
import com.flyingpig.cloudmusic.idempotent.core.spel.IdempotentSpELByMQExecuteHandler;
import com.flyingpig.cloudmusic.idempotent.core.spel.IdempotentSpELByRestAPIExecuteHandler;
import com.flyingpig.cloudmusic.idempotent.enums.IdempotentSceneEnum;
import com.flyingpig.cloudmusic.idempotent.enums.IdempotentTypeEnum;

/**
 * 幂等执行处理器工厂
 */
public final class IdempotentExecuteHandlerFactory {

    /**
     * 获取幂等执行处理器
     *
     * @param scene 指定幂等验证场景类型
     * @param type  指定幂等处理类型
     * @return 幂等执行处理器
     */
    public static IdempotentExecuteHandler getInstance(IdempotentSceneEnum scene, IdempotentTypeEnum type) {
        IdempotentExecuteHandler result = null;
        switch (scene) {
            case RESTAPI:
                switch (type) {
                    case PARAM:
                        result = ApplicationContextHolder.getBean(IdempotentParamService.class);
                        break;
                    case SPEL:
                        result = ApplicationContextHolder.getBean(IdempotentSpELByRestAPIExecuteHandler.class);
                        break;
                    default:
                        break;
                }
                break;
            case MQ:
                result = ApplicationContextHolder.getBean(IdempotentSpELByMQExecuteHandler.class);
                break;
            default:
                break;
        }
        return result;
    }

}
