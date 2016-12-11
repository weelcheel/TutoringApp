package com.csce.tutorapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Migue on 12/7/2016.
 */

public class ConversationAdapter extends ArrayAdapter<ConversationMessage> {

    Context context;
    int layoutResourceId;
    ArrayList<ConversationMessage> data;

    public ConversationAdapter(Context context, int layoutResourceId, ArrayList<ConversationMessage> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        View view;
        if (data.get(position).getAuthorID().equals(FirebaseUtility.getCurrentFirebaseUser().getUid())){
            view = inflater.inflate(R.layout.conversation_msg_layout, null);
        }
        else
            view = inflater.inflate(R.layout.conversation_sendmsg_layout, null);

        TextView msgTxt = (TextView) view.findViewById(R.id.textView);
        msgTxt.setText(data.get(position).getMessage());

        return view;
    }
}
