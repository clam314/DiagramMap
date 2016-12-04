package com.clam314.diagrammap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDiagramMap sm = (SimpleDiagramMap)findViewById(R.id.map);
        sm.setItemList(testItem());
    }

    public static List<SimpleDiagramMap.Item> testItem(){
        String[] itemName = new String[]{"火力","雷装","对空","回避","耐久"};
        List<SimpleDiagramMap.Item> mItemList = new ArrayList<>();
        for(String s: itemName){
            SimpleDiagramMap.Item item = new SimpleDiagramMap.Item();
            item.itemName = s;
            item.value = (float) Math.random()*10;
            mItemList.add(item);
        }
        return mItemList;
    }
}
