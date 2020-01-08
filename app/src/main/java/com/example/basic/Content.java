package com.example.basic;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class Content extends Fragment {
    private Button plus;
    private ListView m_oListView=null;
    private static final int REQUEST_CODE=3;
    List named = new ArrayList();
    List numberd = new ArrayList();
    public  List<PhoneBook>getContacts(Context context){
        // 데이터베이스 혹은 content resolver 를 통해 가져온 데이터를 적재할 저장소를 먼저 정의
        List<PhoneBook> datas = new ArrayList<>();

        // 1. Resolver 가져오기(데이터베이스 열어주기)
        // 전화번호부에 이미 만들어져 있는 ContentProvider 를 통해 데이터를 가져올 수 있음
        // 전화번호부에 이미 만들어져 있는 ContentProvider 를 통해 데이터를 가져올 수 있음
        // 다른 앱에 데이터를 제공할 수 있도록 하고 싶으면 ContentProvider 를 설정
        // 핸드폰 기본 앱 들 중 데이터가 존재하는 앱들은 Content Provider 를 갖는다
        // ContentResolver 는 ContentProvider 를 가져오는 통신 수단
        ContentResolver resolver = context.getContentResolver();

        // 2. 전화번호가 저장되어 있는 테이블 주소값(Uri)을 가져오기
        Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        // 3. 테이블에 정의된 칼럼 가져오기
        // ContactsContract.CommonDataKinds.Phone 이 경로에 상수로 칼럼이 정의
        String[] projection = { ContactsContract.CommonDataKinds.Phone.CONTACT_ID // 인덱스 값, 중복될 수 있음 -- 한 사람 번호가 여러개인 경우
                ,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                ,  ContactsContract.CommonDataKinds.Phone.NUMBER};

        // 4. ContentResolver로 쿼리를 날림 -> resolver 가 provider 에게 쿼리하겠다고 요청
        Cursor cursor = resolver.query(phoneUri, projection, null, null, null);

        // 4. 커서로 리턴된다. 반복문을 돌면서 cursor 에 담긴 데이터를 하나씩 추출
        if(cursor != null){
            while( cursor.moveToNext()){
                // 4.1 이름으로 인덱스를 찾아준다
                int nameIndex = cursor.getColumnIndex(projection[1]);
                int numberIndex = cursor.getColumnIndex(projection[2]);
                // 4.2 해당 index 를 사용해서 실제 값을 가져온다.
                String name = cursor.getString(nameIndex);
                String number = cursor.getString(numberIndex);

                PhoneBook phoneBook = new PhoneBook();
                phoneBook.setName(name);
                phoneBook.setTel(number);

                //addContactDB(name, number);

                datas.add(phoneBook);
            }
        }
        // 데이터 계열은 반드시 닫아줘야 한다.
        cursor.close();
        return datas;
    }

    public void addContactDB(String NAME, String NUMBER){
        String url = "http://773fb44f.ngrok.io/contacts";

        //JSON형식으로 데이터 통신을 진행합니다!
        JSONObject testjson = new JSONObject();
        try {
            //입력해둔 edittext의 id와 pw값을 받아와 put해줍니다 : 데이터를 json형식으로 바꿔 넣어주었습니다.
            testjson.put("name", NAME);
            testjson.put("number", NUMBER);
            String jsonString = testjson.toString(); //완성된 json 포맷

            //이제 전송해볼까요?
            final RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,testjson, new Response.Listener<JSONObject>() {

                //데이터 전달을 끝내고 이제 그 응답을 받을 차례입니다.
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        //받은 json형식의 응답을 받아
                        JSONObject jsonObject = new JSONObject(response.toString());

                        //key값에 따라 value값을 쪼개 받아옵니다.
                        String result = jsonObject.getString("approve");
                        //String resultPassword = jsonObject.getString("approve_pw");

                        if(result.equals("OK")){
                            //Toast.makeText(getContext(),"Success",Toast.LENGTH_SHORT).show();

                        }else{
                            //easyToast("A contact with the following number already exists!");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //서버로 데이터 전달 및 응답 받기에 실패한 경우 아래 코드가 실행됩니다.
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void easyToast(String str){
        Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
//
//            } else {
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.READ_CONTACTS},
//                        1);
//            }
//        }
        List<PhoneBook> phoneBooks = getContacts(getActivity());
        int leng = phoneBooks.size();
        for (int k = 0; k < leng; k++) {
            String addname = phoneBooks.get(k).getname();
            String addnumber = phoneBooks.get(k).getTel();
            addContactDB(addname, addnumber);

            named.add(phoneBooks.get(k).getname());
            numberd.add(phoneBooks.get(k).getTel());
//
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content,container, false);


        //새 연락처

        ArrayList<ItemData> oData = new ArrayList<>();

        int len=named.size();
        for (int i=0; i<len; i++) {
            ItemData oItem = new ItemData();
            oItem.strTitle = named.get(i).toString();
            oItem.strDate = numberd.get(i).toString();
            oData.add(oItem);
        }


// ListView, Adapter 생성 및 연결 ------------------------
        m_oListView = (ListView) view.findViewById(R.id.klist);
        ListAdapter oAdapter = new ListAdapter(oData);
        m_oListView.setAdapter(oAdapter);
        oAdapter.notifyDataSetChanged();


        plus=view.findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity().getApplicationContext(), sub.class);
                startActivityForResult(intent,REQUEST_CODE);
            }

        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
/*
        if(resultCode==-1){
            Toast.makeText(getActivity().getApplicationContext(),"수신 성공",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity().getApplicationContext(),"수신 실패",Toast.LENGTH_SHORT).show();
        }


 */
        if(requestCode ==REQUEST_CODE){
            String str1 =data.getStringExtra("fn");
            String str3 =data.getStringExtra("pn");
            if (str1!=null) {
                named.add(str1);
                numberd.add(str3);

                ArrayList<ItemData> oData = new ArrayList<>();
                int len = named.size();
                for (int i = 0; i < len; i++) {
                    ItemData oItem = new ItemData();
                    oItem.strTitle = named.get(i).toString();
                    oItem.strDate = numberd.get(i).toString();
                    oData.add(oItem);
                }
                ListAdapter oAdapter = new ListAdapter(oData);
                m_oListView.setAdapter(oAdapter);
                oAdapter.notifyDataSetChanged();
            }else{
                ArrayList<ItemData> oData = new ArrayList<>();
                int len = named.size();
                for (int i = 0; i < len; i++) {
                    ItemData oItem = new ItemData();
                    oItem.strTitle = named.get(i).toString();
                    oItem.strDate = numberd.get(i).toString();
                    oData.add(oItem);
                }
                ListAdapter oAdapter = new ListAdapter(oData);
                m_oListView.setAdapter(oAdapter);
                oAdapter.notifyDataSetChanged();

            }

        }
    }
}