package com.example.easytools;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class myadapter extends RecyclerView.Adapter<myviewholder> {
    ArrayList<Model> data;
    Context context;

    public myadapter(ArrayList<Model> data, Context context)
    {
        this.context=context;
        this.data = data;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.single_item,parent,false);
        return  new myviewholder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        final Model temp = data.get(position);
        holder.t1.setText(data.get(position).getHeader());
        holder.t2.setText(data.get(position).getDesc());
        holder.img.setImageResource(data.get(position).getImgname());

//        String t11="Chat with AI";
//        String t22 = "AI Images";

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.t1.getText().equals("Chat AI")) {
                    Intent intent = new Intent(context, ChatAIActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                else if(holder.t1.getText().equals("AI Images")){
                    Intent itent = new Intent(context,AiImageActivity.class);
                    itent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(itent);
                }
                else if(holder.t1.getText().equals("QR Code Scanner")){
                    Intent itent = new Intent(context,QrScannerActivity.class);
                    itent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(itent);
                }
                else if(holder.t1.getText().equals("Qr Code Generator")){
                    Intent itent = new Intent(context,QrGeneratorActivity.class);
                    itent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(itent);
                }
                else if(holder.t1.getText().equals("Calculator")) {
                    Intent itent = new Intent(context, CalculatorActivity.class);
                    itent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(itent);
                }
                else if(holder.t1.getText().equals("Scientific Calci")){
                    Intent itent = new Intent(context,ScientificCalci.class);
                    itent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(itent);
                }



            }

        });

    }


    @Override
    public int getItemCount() {
        return data.size();
    }
}
