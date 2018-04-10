package com.trabajo.carlos.somefoodserver.Model;

import java.util.List;

/**
 * Created by Carlos Prieto on 17/12/2017.
 */

public class MyResponse {

    public long multicast_id;
    public int succes;
    public int failure;
    public int canonical_ids;
    public List<Result> results;

}
