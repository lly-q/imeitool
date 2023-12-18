package com.ziji.imeitool;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.io.PrintStream;
import java.util.List;

/* loaded from: classes3.dex */
public class OutDataAdapter extends ArrayAdapter<Data> {
    ClipboardManager myClipboard;
    private int resourceID;
    TextView tv_new_imei;

    public OutDataAdapter(Context context, int textViewResourceID, List<Data> objects, ClipboardManager myClipboard, TextView tv_new_imei) {
        super(context, textViewResourceID, objects);
        this.resourceID = textViewResourceID;
        this.myClipboard = myClipboard;
        this.tv_new_imei = tv_new_imei;
    }

    @Override // android.widget.ArrayAdapter, android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final Data browser = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(this.resourceID, (ViewGroup) null);
        TextView text0 = (TextView) view.findViewById(R.id.text0);
        TextView text1 = (TextView) view.findViewById(R.id.text1);
        TextView text2 = (TextView) view.findViewById(R.id.text2);
        final Button copy = (Button) view.findViewById(R.id.item_copy);
        if (browser.color == 0) {
            copy.setBackgroundColor(-7829368);
        }
        copy.setOnClickListener(new View.OnClickListener() { // from class: com.ziji.imeitool.-$$Lambda$OutDataAdapter$rPzh3ZIBFxCEi39LpoB8K-oyovk
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                OutDataAdapter.this.lambda$getView$0$OutDataAdapter(browser, copy, view2);
            }
        });
        text0.setText((position + 1) + ".");
        text1.setText(browser.d1);
        text2.setText(browser.d2);
        return view;
    }

    public /* synthetic */ void lambda$getView$0$OutDataAdapter(Data browser, Button copy, View V) {
        PrintStream printStream = System.out;
        printStream.println("browser.d1:" + browser.d1);
        ClipData myClip = ClipData.newPlainText("text", browser.d1);
        this.myClipboard.setPrimaryClip(myClip);
        copy.setBackgroundColor(-7829368);
        browser.color = 0;
        this.tv_new_imei.setText(browser.d1);
        MainActivity.saveList();
    }
}
