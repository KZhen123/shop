package com.service;

import com.entity.Collect;
import com.mapper.CollectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CollectService {
    @Autowired
    private CollectMapper collectMapper;

    public Integer insertCollect(Collect collect){
        return collectMapper.insertCollect(collect);
    }

    public List<Collect> queryAllCollect(Integer page,Integer count,String couserid){
        return collectMapper.queryAllCollect(page,count,couserid);
    }

    public Integer updateCollect(Collect collect){
        return collectMapper.updateCollect(collect);
    }

    public Collect queryCollect(Collect collect){
        return collectMapper.queryCollect(collect);
    }

    public Integer queryCollectCount(String couserid){
        return collectMapper.queryCollectCount(couserid);
    }
}
