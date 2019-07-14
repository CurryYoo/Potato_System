package com.example.kerne.potato;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过HTTP请求获取服务器中数据
 */

public class HttpRequest {
    private static final String url = "http://120.78.130.251:9527/"; //数据库ip
    private static final String picUrl = "http://120.78.130.251:9527/"; //上传图片ip
    public static final String serverUrl = "http://cxk.nicesite.vip/"; //图片服务器url
    private static RequestQueue requestQueue;

    public static void HttpRequest_bigfarm(final String name, Context context, final HttpCallback callback) {
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "bigfarm/getAllBigfarm", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Bigfarm_Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
//                    JSONArray rows = jsonObject.getJSONArray("rows");
//                    Uri uri = getImageURI(serverUrl + jsonObject.getString("img"), cache);
//                    jsonObject.put("uri", uri);
                    callback.onSuccess(jsonObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Bigfarm_Error", error.getMessage(), error);
            }
        });

//        JSONObject jsonObject = new JSONObject();
//        if(name != null){
//            try {
//                jsonObject.put("name", name); //POST数据
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        Log.d("POST_bigfarm", jsonObject.toString());
//
//        //volley进行网络传输
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "bigfarm/getAllBigfarm", jsonObject, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) { //接收数据
//                Log.d("TAG_response", response.toString());
//                callback.onSuccess(response);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("TAG_error_bigfarm", error.getMessage(), error);
//            }
//        });

        requestQueue.add(stringRequest);
    }

    public static Uri getImageURI(String path, File cache) throws Exception {
        String name = path.substring(path.lastIndexOf("."));
        File file = new File(cache, name);
        // 如果图片存在本地缓存目录，则不去服务器下载
        if (file.exists()) {
            return Uri.fromFile(file);//Uri.fromFile(path)这个方法能得到文件的URI
        } else {
            // 从网络上获取图片
            java.net.URL url = new URL(path);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setConnectTimeout(5000);
//            conn.setRequestMethod("GET");
//            conn.setDoInput(true);

            OkHttpClient okHttpClient = new OkHttpClient();
            com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                    .url(url)
                    .build();
            com.squareup.okhttp.Response response = okHttpClient.newCall(request).execute();

            if (response.code() == 200) {

//                InputStream is = conn.getInputStream();
                InputStream is = response.body().byteStream();
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                is.close();
                fos.close();
                // 返回一个URI对象
                return Uri.fromFile(file);
            }
        }
        return null;
    }

    //获取试验田列表
    public static void HttpRequest_farm(final String name, Context context, final HttpCallback callback) {
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

        JSONObject jsonObject = new JSONObject();
        if (name != null) {
            try {
                jsonObject.put("name", name); //POST数据
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d("POST_farm_name", jsonObject.toString());

        //volley进行网络传输
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "farm/getFarmList", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) { //接收数据
                Log.d("TAG_farm", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG_error_farm", error.getMessage(), error);
            }
        });

        requestQueue.add(jsonObjectRequest);
        //requestQueue.add(stringRequest);

    }

    //获取种植图信息
    public static void HttpRequest_map(final String farmlandId, Context context, final HttpCallback callback) {
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

        JSONObject jsonObject = new JSONObject();
        if (farmlandId != null) {
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
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

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

    //下载品种位置信息
    public static void HttpRequest_Species(Context context, final HttpCallback callback) {
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "Block/getAllInnerBlocks", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Species_Response", response);
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
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

    //下载本地品种数据
    public static void HttpRequest_LocalSpecies(final JSONObject jsonObject, Context context, final HttpCallback callback) {
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "localspecies/getLocalSpecies", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("LocalSpecies_response", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("LocalSpecies_error", error.getMessage(), error);
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

        requestQueue.add(jsonObjectRequest);
    }

    //上传品种采集信息
    public static void HttpRequest_SpeciesData(final JSONObject jsonObject, Context context, final HttpCallback callback) {
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

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

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };

        requestQueue.add(jsonObjectRequest);
    }

    //上传图片信息
    public static void doUploadTest(String picPath, String speciesId, String picNo, Context context, final HttpCallback_Str callback) {
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

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
            Log.d("file", "not found");
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

    //上传品种位置规划数据
    public static void HttpRequest_SpeciesList(final JSONArray jsonArray, Context context, final HttpCallback callback) {
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();

//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/Block/putSpeciesPositionToBlock", new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                Log.d("species_response", response);
//                try {
//                    callback.onSuccess(new JSONObject(response));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("species_error", error.getMessage(), error);
//            }
//        }) {
////            @Override
////            protected Map<String, String> getParams() throws AuthFailureError {
////                // 请求参数
////                Map<String, String> map = new HashMap<String, String>();
////                //new 一个Map  参数放到Map中
////                map.put("blockList", fieldId);
////                return map;
////            }
//
//            @Override
//            public byte[] getBody() {
//                return jsonArray.toString();
//            }
//        };

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url + "Block/putSpeciesPositionToBlock", jsonArray, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d("SpeciesList_response", response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("SpeciesList_error", error.getMessage(), error);
            }
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    jsonString = "[" + jsonString + "]"; //由于返回的数据是jsonobject而不是jsonarray，因此需要将其改为jsonarray格式
                    return Response.success(new JSONArray(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);
    }

    public static void HttpRequest_description(final String experimentFieldId, final String description, Context context, final HttpCallback callback) {
//        requestQueue = Volley.newRequestQueue(context);
        requestQueue = SingleRequestQueue.getInstance(context).getRequestQueue();
//        StringRequest s = new StringRequest()
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "experimentfield/updateDesc", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("decription_response", response);
                try {
                    callback.onSuccess(new JSONObject(response));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("description_error", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("experimentFieldId", experimentFieldId);
                map.put("description", description);
                return map;
            }
        };

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("experimentFieldId", experimentFieldId);
            jsonObject.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "experimentfield/updateDescForAndroid", jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("decription_response", response.toString());
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("description_error", error.getMessage(), error);
            }
        });

//        requestQueue.add(stringRequest);
        requestQueue.add(jsonObjectRequest);
    }

    public interface HttpCallback {
        void onSuccess(JSONObject result);
    }

    public interface HttpCallback_Str {
        void onSuccess(String result);
    }
}
