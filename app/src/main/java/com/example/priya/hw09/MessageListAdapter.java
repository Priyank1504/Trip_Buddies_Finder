package com.example.priya.hw09;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.*;

import java.util.Date;
import java.util.List;

/**
 * Created by priyank Verma
 */
public class MessageListAdapter extends ArrayAdapter<MessageDetail> {

    Context context;
    int resID;
    List<MessageDetail> objects;
    FirebaseUser firebaseUser;
    TextView txtName;
    User usr;
    public MessageListAdapter(Context context, int resource, List<MessageDetail> objects, User usr) {
        super(context, resource, objects);
        this.context=context;
        this.resID=resource;
        this.objects=objects;
        this.usr=usr;

    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(resID,parent,false);
        }
        final MessageDetail msgDetail=objects.get(position);


        TextView txtMessage=(TextView)convertView.findViewById(R.id.tvmessage);
        txtName=(TextView)convertView.findViewById(R.id.tvName);
        TextView txtTime=(TextView)convertView.findViewById(R.id.tvTime);
       // txtName.setText(usr.getFname() + " " + usr.getLname());


        txtName.setText(objects.get(position).UserId);

        ImageView imgView=(ImageView)convertView.findViewById(R.id.fileImage);

        final ImageView imgViewComment=(ImageView)convertView.findViewById(R.id.imgcomment);
        ImageView imgViewDelete=(ImageView)convertView.findViewById(R.id.imageDelete);

        imgViewComment.setTag(position);
        imgViewDelete.setTag(position);
        Log.d("msg:" , msgDetail.toString());


        PrettyTime p=new PrettyTime();
        txtTime.setText(p.format(new Date(Long.parseLong(msgDetail.CreatedAt))));

        if(firebaseUser.getEmail().equals(msgDetail.UserId))
        {
            imgViewComment.setVisibility(View.VISIBLE);
            imgViewDelete.setVisibility(View.VISIBLE);
        }
        else
        {
            imgViewComment.setVisibility(View.INVISIBLE);
            imgViewDelete.setVisibility(View.INVISIBLE);
        }
        if(msgDetail.Type.equalsIgnoreCase("IMAGE"))
        {
            txtMessage.setVisibility(View.INVISIBLE);
            imgView.setVisibility(View.VISIBLE);

            Picasso.with(context).load(msgDetail.FileThumbnailId).into(imgView);
        }
        else if (msgDetail.Type.equalsIgnoreCase("TEXT"))
        {
            txtMessage.setVisibility(View.VISIBLE);
            imgView.setVisibility(View.INVISIBLE);
            txtMessage.setText(msgDetail.Comment);
        }
        else
        {
        }

        Log.d("COMMENT",msgDetail.toString());

        final View finalConvertView = convertView;
        imgViewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pos=(Integer)v.getTag();
                imgViewComment.setEnabled(false);

                final LinearLayout linearLayout=(LinearLayout) finalConvertView.findViewById(R.id.VerLayout);
                LinearLayout horizontalLayout=new LinearLayout(context);
                horizontalLayout.setTag(pos);
                horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                horizontalLayout.setLayoutParams(params);
                horizontalLayout.setId(pos);

                final EditText editText=new EditText(context);
                LinearLayout.LayoutParams params1=new LinearLayout.LayoutParams(200, ViewGroup.LayoutParams.WRAP_CONTENT);
                editText.setLayoutParams(params1);
                editText.setId(pos+1000);

                horizontalLayout.addView(editText);
                ImageView img=new ImageView(context);
                img.setId(pos+1001);
                LinearLayout.LayoutParams params2=new LinearLayout.LayoutParams(40, 40);
                img.setLayoutParams(params2);
                horizontalLayout.addView(img);
                img.setImageResource(R.drawable.send);

                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(editText.getText().toString().length()>0)
                        {
                            int count=linearLayout.getChildCount();
                            View view;
                            int viewPos=-1;
                            for(int i=0;i<count;i++)
                            {
                                view=linearLayout.getChildAt(i);
                                if(view.getId()==pos)
                                {
                                    viewPos=i;

                                    break;
                                }
                            }
                            if(viewPos>=0)
                                linearLayout.removeViewAt(viewPos);
                            imgViewComment.setEnabled(true);

                            ChatRoomActivity activity=(ChatRoomActivity) context;

                            activity.sendCommentForMessage(msgDetail,editText.getText().toString());
                        }
                        Log.d("Demo","inside image on Click");

                    }
                });
                linearLayout.addView(horizontalLayout);
            }
        });
        imgViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((usr.getFname() + " " + usr.getLname()).equals(txtName.getText())) {
                    int pos = (Integer) v.getTag();
                    ChatRoomActivity activity = (ChatRoomActivity) context;

                    activity.deleteMessage(msgDetail);
                }
            }
        });


        return convertView;
    }
}
