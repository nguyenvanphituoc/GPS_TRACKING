package com.trongtri.hcmute.myapplication2.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 12/29/2017.
 */

public class ParseJsonPolyline {

    public List<LatLng> layDanhSachToaDo(String dataJson)
    {
        List<LatLng> latLngList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(dataJson);
            JSONArray routes = jsonObject.getJSONArray("routes");
            for(int i = 0; i < routes.length(); i++)
            {
                JSONObject jsonObjectArray = routes.getJSONObject(i);
                JSONObject overview_polyline = jsonObjectArray.getJSONObject("overview_polyline");
                String points = overview_polyline.getString("points");

                latLngList = PolyUtil.decode(points);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  latLngList;
    }
}

