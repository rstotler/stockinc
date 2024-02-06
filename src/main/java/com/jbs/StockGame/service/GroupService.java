package com.jbs.StockGame.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jbs.StockGame.entity.Group;

@Service
public class GroupService {
    private List<Group> groups = new ArrayList<>();

    public void create(String groupName, String groupSymbol, String founder) {
        groups.add(new Group(groupName, groupSymbol, founder));
    }

    public List<Group> findAll() {
        return groups;
    }
}
