package com.example.projecthelloondo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.net.URL;
import java.util.HashMap;

//참고 사이트 : https://velog.io/@dlrmwl15/%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EC%9B%B9%EC%97%90%EC%84%9C-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EA%B0%80%EC%A0%B8%EC%98%A4%EA%B8%B0


/*
스레드를 사용하기 위해 AsyncTask를 상속하여 새로운 클래스를 정의
이 클래스를 이용해 객체를 생성할 때는 이미지가 있는 주소와 함께 이 이미지를 다운로드받은 후 화면에 보여줄 때 사용할
이미지뷰(ImageView) 객체를 파라미터로 전달
*/
public class ImageLoadTask extends AsyncTask<Void,Void, Bitmap> {

    private String urlStr;
    private ImageView imageView;
    private static HashMap<String, Bitmap> bitmapHash = new HashMap<String, Bitmap>();

    //생성자
    public ImageLoadTask(String urlStr, ImageView imageView) {
        this.urlStr = urlStr;
        this.imageView = imageView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //웹서버의 이미지 데이터를 받아 비트맵 객체로 만들어주는 메소드
    @Override
    protected Bitmap doInBackground(Void... voids) {
        Bitmap bitmap = null;
        try {
            if (bitmapHash.containsKey(urlStr)) {
                Bitmap oldbitmap = bitmapHash.remove(urlStr);
                if(oldbitmap != null) {
                    oldbitmap.recycle();
                    oldbitmap = null;
                }
            }

            URL url = new URL(urlStr);

            //BitmapFactory 클래스의 decodeStream 메소드를 사용하면 간단한 코드 만으로도 비트맵 객체를 만들어줄 수 있음
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            bitmapHash.put(urlStr,bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    //비트맵 객체로 변환하고 나면 메인 스레드에서 이미지뷰에 표시
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        imageView.setImageBitmap(bitmap);
        imageView.invalidate();
    }

}

