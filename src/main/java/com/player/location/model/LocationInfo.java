package com.player.location.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class LocationInfo {

    public int x;
    public int y;
    public String toward;
    public int index;
    public List<String> orderList;



}
