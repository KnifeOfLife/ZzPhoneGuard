package com.example.zzphoneguard.engine;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

import com.example.zzphoneguard.utils.EncryptTools;
import com.example.zzphoneguard.utils.JsonStrTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by 狗蛋儿 on 2016/9/22.
 * 短信的备份和还原的业务类
 *
 */
public class SmsEngine {
    /**
     * 通过该接口设置进度条的显示
     */
    public interface BaikeProgress{
        /**
         * 进度条的显示
         */
        void show();

        /**
         * 进度条的关闭
         */
        void dismiss();

        /**
         * 设置进度条的进度总值
         * @param counts
         * 进度条的进度总值
         */
        void setMax(int counts);

        /**
         * 设置进度条的当前进度
         * @param progress
         * 进度条的当前进度
         */
        void setProgress(int progress);
    }

    //进度条的进度数
    private static class Data{
        int progress = 0;
    }

    /**
     * 通过子线程来做短信的备份,并使用xml文件保存信息
     * @param context
     * 上下文
     * @param pd
     * 设置进度条的接口，该接口的UI在主线程中运行
     */
    public static void smsBaikeXml(final Activity context, final BaikeProgress pd){
        new Thread(){
            @Override
            public void run() {
                Uri uri = Uri.parse("content://sms");
                final Cursor cursor = context.getContentResolver().query(uri, new String[]{"address","date","body","type"},
                        null, null, " _id desc");
                File file = new File(context.getCacheDir().getPath(),"sms.xml");
                Log.d("hahahahah",context.getCacheDir().getPath());
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    PrintWriter out = new PrintWriter(fos);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.setMax(cursor.getCount());
                            pd.show();
                        }
                    });
                    final Data data = new Data();
                    out.println("<smses count='" + cursor.getCount() + "'>");
                    while (cursor.moveToNext()){
                        data.progress++;
                        SystemClock.sleep(100);
                        out.println("<sms>");
                        out.println("<address>" + cursor.getString(0) + "</address>");
                        out.println("<date>" + cursor.getString(1) + "</date>");
                        out.println("<body>" + cursor.getString(2) + "</body>");
                        out.println("<type>" + cursor.getString(3) + "</type>");
                        out.println("</sms>");
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setProgress(data.progress);
                            }
                        });

                    }
                    out.println("</smses>");
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });
                    out.flush();
                    out.close();//关闭流
                    cursor.close();//关闭游标

                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 通过子线程来做短信的备份，并使用json文件保存信息
     *
     * @param context
     * @param pd
     *            通过接口回调备份的数据（所有回调方法都在主线程中执行）
     */
    public static void smsBaikeJson(final Activity context,
                                    final BaikeProgress pd) {
        new Thread() {
            @Override
            public void run() {
                // 1,通过内容提供者获取到短信
                Uri uri = Uri.parse("content://sms");
                // 获取电话记录的联系人游标
                final Cursor cursor = context.getContentResolver().query(uri,
                        new String[] { "address", "date", "body", "type" },
                        null, null, " _id desc");

                if (cursor.getCount()==0){
                    return;
                }

                File file = new File(context.getCacheDir().getPath(), "sms.json");
                try {
                    FileOutputStream fos = new FileOutputStream(file);

                    PrintWriter out = new PrintWriter(fos);
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.show();
                            pd.setMax(cursor.getCount());// 设置进度条总进度
                        }
                    });
                    final Data data = new Data();
                    out.println("{\"count\":\"" + cursor.getCount() + "\"");
                    out.println(",\"smses\":[");
                    while (cursor.moveToNext()) {
                        data.progress++;
                        SystemClock.sleep(100);
                        if (cursor.getPosition() == 0) {
                            out.println("{");
                        } else {
                            out.println(",{");
                        }
                        out.println("\"address\":\"" + cursor.getString(0) + "\",");
                        out.println("\"date\":\"" + cursor.getString(1) + "\",");
                        out.println("\"body\":\"" + EncryptTools.encrypt(JsonStrTools.changeStr(cursor.getString(2))) + "\",");
                        out.println("\"type\":\"" + cursor.getString(3) + "\"");
                        out.println("}");
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setProgress(data.progress);
                            }
                        });

                    }
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });
                    out.println("]}");

                    out.flush();
                    out.close();// 关闭流
                    cursor.close();// 关闭游标
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 通过子线程来做短信的还原json格式
     *
     * @param context
     * @param pd
     *            通过接口回调备份的数据（所有回调方法都在主线程中执行）
     */
    public static void smsResumnJson(final Activity context,
                                     final BaikeProgress pd) {
        final Data data = new Data();
        new Thread() {
            @Override
            public void run() {
                // 1,通过内容提供者保存短信
                Uri uri = Uri.parse("content://sms");

                // 2,获取备份的短信
                try {
                    FileInputStream fis = new FileInputStream(new File(
                            context.getCacheDir().getPath(),
                            "sms.json"));
                    // json数据的合并
                    StringBuilder jsonSmsStr = new StringBuilder();
                    // io流的封装 把字节流封装成缓冲的字符流
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(fis));

                    String line = reader.readLine();
                    while (line != null) {
                        jsonSmsStr.append(line);
                        line = reader.readLine();
                    }

                    // 解析json数据
                    JSONObject jsonObj = new JSONObject(jsonSmsStr.toString());
                    //短信的个数
                    final int counts = Integer.parseInt(jsonObj.getString("count"));
                    System.out.println();

                    //设置回调结果的 show和 setMax方法
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.show();
                            pd.setMax(counts);// 设置进度条总进度

                        }
                    });

                    //循环读取短信
                    JSONArray jarray = (JSONArray) jsonObj.get("smses");
                    for (int i = 0; i < counts ;i++) {
                        data.progress = i;
                        //获取一条短信
                        JSONObject smsjson = jarray.getJSONObject(i);

                        ContentValues values = new ContentValues();
                        values.put("address", smsjson.getString("address"));
                        values.put("body", EncryptTools.decryption( smsjson.getString("body")));
                        values.put("date", smsjson.getString("date"));
                        values.put("type", smsjson.getString("type"));

                        //往短信数据中加一条记录
                        context.getContentResolver().insert(uri, values);

                        //回调结果当前进度
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                pd.setProgress(data.progress);
                            }
                        });
                    }
                    reader.close();// 关闭io流

                    //回调备份完成的结果
                    context.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            pd.dismiss();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

}
