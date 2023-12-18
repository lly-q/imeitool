package com.ziji.imeitool;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

/* loaded from: classes3.dex */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static ArrayList<Data> outdata = new ArrayList<>();
    static String parent_path;
    String imsi;
    ClipboardManager myClipboard;
    OutDataAdapter outDataAdapter;
    ListView out_data_list;
    OutDataAdapter selectDataAdapter;
    ListView select_data_list;
    TextView tv_imsi;
    EditText tv_new_imei;
    ArrayList<Data> selectdata = new ArrayList<>();
    ArrayList<Data> select_new_data = new ArrayList<>();

    String getImsiInSelectData() {
        Iterator<Data> it = this.selectdata.iterator();
        while (it.hasNext()) {
            Data d = it.next();
            if (d.d2.equals(this.imsi)) {
                String imei = d.d1;
                return imei;
            }
        }
        return null;
    }

    void initTextView() {
        TextView tv_imei1 = (TextView) findViewById(R.id.tv_imei1);
        String imei1 = Util.getDeviceIdByReflect(this, 0);
        tv_imei1.setText(imei1);
        TextView tv_imei2 = (TextView) findViewById(R.id.tv_imei2);
        String imei2 = Util.getDeviceIdByReflect(this, 1);
        tv_imei2.setText(imei2);
        this.tv_imsi = (TextView) findViewById(R.id.tv_imsi);
        String imsiByReflect = Util.getImsiByReflect(this);
        this.imsi = imsiByReflect;
        if (imsiByReflect.isEmpty()) {
            this.imsi = "no imsi get";
        }
        this.tv_imsi.setText(this.imsi);
        this.tv_new_imei = (EditText) findViewById(R.id.tv_new_imei);
    }

    void selectData() {
        this.select_new_data.clear();
        Iterator<Data> it = this.selectdata.iterator();
        while (it.hasNext()) {
            Data data = it.next();
            if (data.d2.equals(this.imsi)) {
                Data tmp = new Data();
                tmp.d1 = data.d1;
                tmp.d2 = "匹配的IMSI";
                tmp.color = data.color;
                this.select_new_data.add(tmp);
            }
        }
    }

    void initListView() {
        this.out_data_list = (ListView) findViewById(R.id.out_data_list);
        this.select_data_list = (ListView) findViewById(R.id.select_data_list);
        outdata.clear();
        this.selectdata.clear();
        getFileContent(new File(parent_path + "/imei.txt"), outdata);
        getFileContent(new File(parent_path + "/imsi.txt"), this.selectdata);
        OutDataAdapter outDataAdapter = new OutDataAdapter(this, R.layout.out_data_list_item, outdata, this.myClipboard, this.tv_new_imei);
        this.outDataAdapter = outDataAdapter;
        this.out_data_list.setAdapter((ListAdapter) outDataAdapter);
        selectData();
        OutDataAdapter outDataAdapter2 = new OutDataAdapter(this, R.layout.out_data_list_item, this.select_new_data, this.myClipboard, this.tv_new_imei);
        this.selectDataAdapter = outDataAdapter2;
        this.select_data_list.setAdapter((ListAdapter) outDataAdapter2);
    }

    void initHeadButton() {
        initListView();
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:16:0x0084 -> B:59:0x00b2). Please submit an issue!!! */
    public static void saveList() {
        StringBuffer sb = new StringBuffer();
        Iterator<Data> it = outdata.iterator();
        while (it.hasNext()) {
            Data d = it.next();
            sb.append(d.d1 + "--" + d.d2 + "--" + d.color + "\r\n");
        }
        BufferedWriter bw = null;
        FileOutputStream fos = null;
        try {
            try {
                try {
                    File file = new File(parent_path + "/imei.txt");
                    fos = new FileOutputStream(file);
                    bw = new BufferedWriter(new OutputStreamWriter(fos));
                    bw.write(sb.toString());
                    bw.flush();
                    try {
                        bw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fos.close();
                } catch (FileNotFoundException e2) {
                    e2.printStackTrace();
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    if (fos == null) {
                        return;
                    }
                    fos.close();
                } catch (IOException e4) {
                    e4.printStackTrace();
                    if (bw != null) {
                        try {
                            bw.close();
                        } catch (IOException e5) {
                            e5.printStackTrace();
                        }
                    }
                    if (fos == null) {
                        return;
                    }
                    fos.close();
                }
            } catch (IOException e6) {
                e6.printStackTrace();
            }
        } catch (Throwable th) {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e7) {
                    e7.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e8) {
                    e8.printStackTrace();
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(16);
        String path1 = Environment.getExternalStorageDirectory().getPath();
        parent_path = path1 + "/imei";
        PrintStream printStream = System.out;
        printStream.println("parent_path:" + parent_path);
        File dirF = new File(parent_path);
        if (!dirF.exists() || !dirF.isDirectory()) {
            dirF.mkdir();
        }
        this.myClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initTextView();
        initHeadButton();
        Button create_random = (Button) findViewById(R.id.create_random);
        create_random.setOnClickListener(new View.OnClickListener() { // from class: com.ziji.imeitool.-$$Lambda$MainActivity$64G4UXLHXgGZH9TdnRlM-LpcFRQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$0$MainActivity(view);
            }
        });
        Button out_nume = (Button) findViewById(R.id.out_nume);
        out_nume.setOnClickListener(new View.OnClickListener() { // from class: com.ziji.imeitool.-$$Lambda$MainActivity$Ckfigszku6fRCOoBVveNy1KM2qo
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$1$MainActivity(view);
            }
        });
        Button write_nume = (Button) findViewById(R.id.write_nume);
        write_nume.setOnClickListener(new View.OnClickListener() { // from class: com.ziji.imeitool.-$$Lambda$MainActivity$4Pb1eC_qJVMJX3cgJD7WWF7h-Zk
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$2$MainActivity(view);
            }
        });
        Button open_appx = (Button) findViewById(R.id.open_appx);
        open_appx.setOnClickListener(new View.OnClickListener() { // from class: com.ziji.imeitool.-$$Lambda$MainActivity$Sgy5jaldAVotN84U9b6Bqd9Yedg
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$3$MainActivity(view);
            }
        });
        Button bak_write = (Button) findViewById(R.id.bak_write);
        bak_write.setOnClickListener(new View.OnClickListener() { // from class: com.ziji.imeitool.-$$Lambda$MainActivity$gPq_oY4jHL0kj7D4WBkBKt45pbI
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$4$MainActivity(view);
            }
        });
        Button select_imsi = (Button) findViewById(R.id.select_imsi);
        select_imsi.setOnClickListener(new View.OnClickListener() { // from class: com.ziji.imeitool.-$$Lambda$MainActivity$aLioWuT9bKKTyl_yvTPauSfKibo
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                MainActivity.this.lambda$onCreate$5$MainActivity(view);
            }
        });
    }

    public /* synthetic */ void lambda$onCreate$0$MainActivity(View v) {
        String imei = Util.getBeginStr() + Util.getRandomNumber(12);
        String x = packIMEI(imei) + "";
        ClipData myClip = ClipData.newPlainText("text", imei + x);
        this.myClipboard.setPrimaryClip(myClip);
        this.tv_new_imei.setText(imei + x);
    }

    public /* synthetic */ void lambda$onCreate$1$MainActivity(View v) {
        this.out_data_list.setVisibility(View.VISIBLE);
        this.select_data_list.setVisibility(View.GONE);
    }

    public /* synthetic */ void lambda$onCreate$2$MainActivity(View v) {
        String imei = this.tv_new_imei.getText().toString();
        if (imei.isEmpty()) {
            Toast.makeText(this, "新串没有数据", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipData myClip = ClipData.newPlainText("text", imei);
        this.myClipboard.setPrimaryClip(myClip);
        getPackageManager();
        if (checkPackInfo("com.zudiqq1")) {
            ComponentName componetName = new ComponentName("com.zudiqq1", "number1.nizijitihuan");
            Intent intent = new Intent();
            intent.setComponent(componetName);
            startActivity(intent);
            return;
        }
        Toast.makeText(this, "没有安装com.zudiqq1", Toast.LENGTH_SHORT).show();
    }

    public /* synthetic */ void lambda$onCreate$3$MainActivity(View v) {
        String imei = this.tv_new_imei.getText().toString();
        if (imei.isEmpty()) {
            Toast.makeText(this, "新串没有数据", Toast.LENGTH_SHORT).show();
            return;
        }
        ClipData myClip = ClipData.newPlainText("text", imei);
        this.myClipboard.setPrimaryClip(myClip);
        if (checkPackInfo("com.zudiqq2")) {
            ComponentName componetName = new ComponentName("com.zudiqq2", "number2.nizijitihuan");
            Intent intent = new Intent();
            intent.setComponent(componetName);
            startActivity(intent);
            return;
        }
        Toast.makeText(this, "没有安装com.zudiqq2", Toast.LENGTH_SHORT).show();
    }

    public /* synthetic */ void lambda$onCreate$4$MainActivity(View v) {
        String x = getImsiInSelectData();
        if (x != null) {
            Toast.makeText(this, "已经存在配对的IMEI", Toast.LENGTH_SHORT).show();
            return;
        }
        Data d = new Data();
        d.d1 = this.tv_new_imei.getText().toString();
        d.d2 = this.imsi;
        d.color = 0;
        this.selectdata.add(d);
        saveToSelectFile(parent_path + "/imsi.txt", this.tv_new_imei.getText().toString() + "--" + this.imsi);
        Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
    }

    public /* synthetic */ void lambda$onCreate$5$MainActivity(View v) {
        String imsiByReflect = Util.getImsiByReflect(this);
        this.imsi = imsiByReflect;
        if (imsiByReflect.isEmpty()) {
            this.imsi = "no imsi get";
            return;
        }
        this.tv_imsi.setText(this.imsi);
        this.out_data_list.setVisibility(View.GONE);
        this.select_data_list.setVisibility(View.VISIBLE);
        String x = getImsiInSelectData();
        if (x != null) {
            this.tv_new_imei.setText(x);
            Toast.makeText(this, "有配对的IMEI", Toast.LENGTH_SHORT).show();
        }
        selectData();
        this.selectDataAdapter.notifyDataSetChanged();
    }

    private boolean checkPackInfo(String packname) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(packname, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo != null;
    }

    private void getFileContent(File file, ArrayList<Data> temp) {
        if (!file.isDirectory() && file.getName().endsWith("txt")) {
            try {
                InputStream instream = new FileInputStream(file);
                InputStreamReader inputreader = new InputStreamReader(instream, "UTF-8");
                BufferedReader buffreader = new BufferedReader(inputreader);
                while (true) {
                    String line = buffreader.readLine();
                    if (line != null) {
                        String[] data = line.split("--");
                        Data d = new Data();
                        if (data.length == 3) {
                            d.d1 = data[0];
                            d.d2 = data[1];
                            d.color = Integer.parseInt(data[2]);
                        } else if (data.length == 2) {
                            d.d1 = data[0];
                            d.d2 = data[1];
                            d.color = 1;
                        } else {
                            d.d1 = data[0];
                            d.d2 = "";
                            d.color = 1;
                        }
                        temp.add(d);
                    } else {
                        instream.close();
                        return;
                    }
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(this, file.getName() + "不存在", Toast.LENGTH_SHORT).show();
            } catch (UnsupportedEncodingException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                Log.d("TestFile", e3.getMessage());
            }
        }
    }

    public static void saveToSelectFile(String fileName, String content) {
        try {
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content + "\r\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isIMEI(String imei) {
        char[] imeiChar = imei.toCharArray();
        int resultInt = 0;
        int i = 0;
        while (i < imeiChar.length - 1) {
            int a = Integer.parseInt(String.valueOf(imeiChar[i]));
            int i2 = i + 1;
            int temp = Integer.parseInt(String.valueOf(imeiChar[i2])) * 2;
            int b = temp < 10 ? temp : temp - 9;
            resultInt += a + b;
            i = i2 + 1;
        }
        int resultInt2 = resultInt % 10;
        int resultInt3 = resultInt2 == 0 ? 0 : 10 - resultInt2;
        int crc = Integer.parseInt(String.valueOf(imeiChar[14]));
        return resultInt3 == crc;
    }

    public static int packIMEI(String imei) {
        char[] imeiChar = imei.toCharArray();
        int resultInt = 0;
        int i = 0;
        while (i < imeiChar.length - 1) {
            int a = Integer.parseInt(String.valueOf(imeiChar[i]));
            int i2 = i + 1;
            int temp = Integer.parseInt(String.valueOf(imeiChar[i2])) * 2;
            int b = temp < 10 ? temp : temp - 9;
            resultInt += a + b;
            i = i2 + 1;
        }
        int resultInt2 = resultInt % 10;
        return resultInt2 == 0 ? 0 : 10 - resultInt2;
    }
}
