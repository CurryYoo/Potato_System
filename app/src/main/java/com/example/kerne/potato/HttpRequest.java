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
    private static String url = "http://120.78.130.251:9527/";
    private static RequestQueue requestQueue;

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

//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "farm/getFarmlist", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("general_response", response);
//                try {
//                    callback.onSuccess(new JSONObject(response));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("general_error", error.getMessage(), error);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                // 请求参数
//                Map<String, String> map = new HashMap<String, String>();
//                //new 一个Map  参数放到Map中
//                if (name != null) {
//                    map.put("name", name);
//                }
//                return map;
//            }
//        };

        requestQueue.add(jsonObjectRequest);
        //requestQueue.add(stringRequest);

    }

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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "/experimentfield/getExperimentFieldList", jsonObject, new Response.Listener<JSONObject>() {
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

//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "farm/getFarmlist", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("general_response", response);
//                try {
//                    callback.onSuccess(new JSONObject(response));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("general_error", error.getMessage(), error);
//            }
//        }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                // 请求参数
//                Map<String, String> map = new HashMap<String, String>();
//                //new 一个Map  参数放到Map中
//                if (name != null) {
//                    map.put("name", name);
//                }
//                return map;
//            }
//        };

        requestQueue.add(jsonObjectRequest);
        //requestQueue.add(stringRequest);

    }

    public static void HttpRequest_species(final String fieldId, Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);

//        JSONObject jsonObject = new JSONObject();
//
//        Map<String, String> merchant = new HashMap<String, String>();
//        merchant.put("fieldId", fieldId);
//        JSONObject jsonObject1 = new JSONObject(merchant);
//
//        try {
//            jsonObject.put("fieldId", fieldId);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        Log.d("POST_field", jsonObject.toString());
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "Block/getInnerSpeciesByFieldId", jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d("TAG_response", response.toString());
//                callback.onSuccess(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG_error", error.getMessage(), error);
//            }
//        });
//        Log.d("TAG_request", jsonObjectRequest.toString());

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

    public static void HttpRequest_SpeciesData(final JSONObject jsonObject, Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "SpeciesCommontest/addCommontest", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG_response", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG_error", error.getMessage(), error);
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "updatePlotData", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("updatePlotData_response", response);
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("farmland_error", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 请求参数
                Map<String, String> map = new HashMap<String, String>();
                //new 一个Map  参数放到Map中
                try {
                    map.put("plotId", jsonObject.getString("plotId"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return map;
            }
        };

        requestQueue.add(jsonObjectRequest);
        //requestQueue.add(stringRequest);
    }

    //==========================================

    public static void HttpRequest_farmland(final String farmlandId, Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("farmlandId", farmlandId);
            jsonObject.put("startYear", 2010);
            jsonObject.put("endYear", 2018);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("POST_farmland", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "getShotList", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG_response", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG_error", error.getMessage(), error);
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "getShotList", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("farmland_response", response);
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("farmland_error", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 请求参数
                Map<String, String> map = new HashMap<String, String>();
                //new 一个Map  参数放到Map中
                map.put("farmlandId", farmlandId);
                return map;
            }
        };

        //requestQueue.add(jsonObjectRequest);
        requestQueue.add(stringRequest);
    }

    public static void HttpRequest_shot(final String shotId, Context context, final HttpCallback callback) {
        requestQueue = Volley.newRequestQueue(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("shotId", shotId);
            jsonObject.put("experimentName", "");
            jsonObject.put("fieldName", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("POST_shot", jsonObject.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "getFieldList", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("TAG_response", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG_error", error.getMessage(), error);
            }
        });
        Log.d("TAG_request0", jsonObjectRequest.toString());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "getFieldList", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("shot_response", response);
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("shot_error", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 请求参数
                Map<String, String> map = new HashMap<String, String>();
                //new 一个Map  参数放到Map中
                map.put("shotId", shotId);
                return map;
            }
        };

        //requestQueue.add(jsonObjectRequest);
        requestQueue.add(stringRequest);
    }


    public static void doUploadTest(String picPath, Context context, final HttpCallback_Str callback) {
        requestQueue = Volley.newRequestQueue(context);

        String path = picPath;
        Log.e("zb", "picPath=" + picPath);
        //String url = "http://app.sod90.com/xxx/upload/app_upload"; //换成自己的测试url地址
        Map<String, String> params = new HashMap<String, String>();
        params.put("speciesId", "1110");
        Log.e("zb", "params=" + params);
        File f1 = new File(path);
        Log.e("zb", "f1=" + f1.toString());
//    File f2 = new File(path);
//    Log.e("zb","f2="+f2.toString());

        if (!f1.exists()) {
            //Toast.makeText(getApplicationContext(), "图片不存在，测试无效", Toast.LENGTH_SHORT).show();
            return;
        }
        List<File> f = new ArrayList<File>();
        f.add(f1);
//    f.add(f2);
        MultipartRequest request = new MultipartRequest(url + "species/updateSpeciesPic1", new Response.Listener<String>() {

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
        }, "f_file[]", f, params); //注意这个key必须是f_file[],后面的[]不能少
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
