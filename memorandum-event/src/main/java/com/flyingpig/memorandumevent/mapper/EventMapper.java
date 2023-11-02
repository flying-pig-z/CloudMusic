package com.flyingpig.memorandumevent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.flyingpig.memorandumevent.dataobject.enetity.Event;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EventMapper extends BaseMapper<Event> {

}
