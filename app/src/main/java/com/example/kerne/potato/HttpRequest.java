package com.example.kerne.potato;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过HTTP请求获取服务器中数据
 */

public class HttpRequest {
    private static String url = "http://120.78.130.251:9527/"; //数据库ip
    private static String picUrl = "http://10.103.241.85:9527/"; //图片服务器ip
    private static RequestQueue requestQueue;

    //获取试验田列表
    public static void HttpRequest_general(final String name, Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        if(name != null){
            try {
                jsonObject.put("name", name); //POST数据
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("POST_general", jsonObject.toString());

        //volley进行网络传输
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "farm/getFarmList", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { //接收数据
                Log.d("TAG_response", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG_error", error.getMessage(), error);
            }
        });

        requestQueue.add(jsonObjectRequest);
        //requestQueue.add(stringRequest);

    }

    //获取种植图信息
    public static void HttpRequest_map(final String farmlandId, Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);

        JSONObject jsonObject = new JSONObject();
        if(farmlandId != null){
            try {
                jsonObject.put("farmlandId", farmlandId); //POST数据
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("POST_general", jsonObject.toString());

        //volley进行网络传输
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "experimentfield/getExperimentFieldList", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { //接收数据
                Log.d("TAG_response", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG_error", error.getMessage(), error);
            }
        });

        requestQueue.add(jsonObjectRequest);
        //requestQueue.add(stringRequest);

    }

    //获取品种信息
    public static void HttpRequest_species(final String fieldId, Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "Block/getInnerSpeciesByFieldId", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("species_response", response);
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("species_error", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 请求参数
                Map<String, String> map = new HashMap<String, String>();
                //new 一个Map  参数放到Map中
                map.put("fieldId", fieldId);
                return map;
            }
        };

        //requestQueue.add(jsonObjectRequest);
        requestQueue.add(stringRequest);
    }

    public static void HttpRequest_Species(Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "Block/getAllInnerBlocks", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Species_Response", response);
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Species_Error", error.getMessage(), error);
            }
        });

        requestQueue.add(stringRequest);
    }

    //上传品种采集信息
    public static void HttpRequest_SpeciesData(final JSONObject jsonObject, Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "SpeciesCommontest/updateCommontest", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("SpeciesData_response", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SpeciesData_error", error.getMessage(), error);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        Log.d("TAG_request", jsonObjectRequest.toString());

        requestQueue.add(jsonObjectRequest);
    }

    //上传图片信息
    public static void doUploadTest(String picPath, String speciesId, String picNo, Context context, final HttpCallback_Str callback) {
        requestQueue = Volley.newRequestQueue(context);

        String path = picPath;
        Log.e("zb", "img=" + picPath);
        //String url = "http://app.sod90.com/xxx/upload/app_upload"; //换成自己的测试url地址
        Map<String, String> params = new HashMap<String, String>();
        params.put("speciesId", speciesId);
        params.put("picNo", picNo);
        Log.e("zb", "params=" + params);
        File f1 = new File(path);
        Log.e("zb", "f1=" + f1.toString());

        if (!f1.exists()) {
            //Toast.makeText(getApplicationContext(), "图片不存在，测试无效", Toast.LENGTH_SHORT).show();
            Log.d("file","not found");
            return;
        }
        List<File> f = new ArrayList<File>();
        f.add(f1);
//    f.add(f2);
        MultipartRequest request = new MultipartRequest(picUrl + "localspecies/updateSpeciesPic", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                //Toast.makeText(getApplicationContext(), "uploadSuccess,response = " + response, Toast.LENGTH_SHORT).show();
                Log.e("zb", "success,response = " + response);
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(), "uploadError,response = " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("zb", "error,response = " + error.getMessage());
            }
        }, "file", f1, params); //注意这个key必须是f_file[],后面的[]不能少

        //mSingleQueue.add(request);
        requestQueue.add(request);
    }

    public interface HttpCallback {
        void onSuccess(JSONObject result);
    }

    public interface HttpCallback_Str {
        void onSuccess(String result);
    }
}
