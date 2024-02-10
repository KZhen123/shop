package com.mapper;

import com.entity.Collect;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CollectMapper {

    Integer insertCollect(Collect collect);

    List<Collect> queryAllCollect(@Param("page") Integer page, @Param("count") Integer count, @Param("couserid") String couserid);

    Integer updateCollect(Collect collect);

    Collect queryCollect(Collect collect);

    Integer queryCollectCount(String couserid);
}
