package me.shuza.textrecognization;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.io.InputStream;
import java.util.Iterator;

import static java.sql.Types.NULL;

public class getLicense extends AppCompatActivity {
    private TextView textView;
    private String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        Intent intent = getIntent();
        data = intent.getStringExtra("license");

        textView = (TextView)findViewById(R.id.textView);

        Log.e("","first data = "+ data);

        data = data.replaceAll(",","");
        data = data.replaceAll("]","");
        data = data.replaceAll("\\[","");
        data = data.replaceAll(" ","");
        Log.e("","re data = "+data);
        textView.setText("인식한 License Number = "+data);
        readExcelFileFromAssets();
    }


    public void readExcelFileFromAssets() {
        boolean exist = false;
        String A="", B="", C="", D="";
        try {
            // Asset 폴더 매니저 생성
            AssetManager assetManager = getAssets();
            //  AssetManager에 들어있는 파일 지정
            InputStream myInput = assetManager.open("PreviewFilesattachment(10).xls");
            // POI 라이브러리 오브젝트 생성
            POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
            // POI 라이브러리 사용
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            // 엑셀파일의 첫번째 시트를 가져온다.
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            // 첫번째 시트의 row를 가져온다.
            Iterator<Row> rowIter = mySheet.rowIterator();
            int rowno =0;
            textView.append("\n");
            while (rowIter.hasNext()) {
                Log.e(""," row no "+ rowno );
                HSSFRow myRow = (HSSFRow) rowIter.next();

                if(rowno == 0){
                    //그다음 컬럼이 있는지 확인해주는 메서드
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno =0;
                    //컬럼 넘버가 있으면 실행
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno==0){
                            A = myCell.toString();
                        }else if (colno==1){
                            B = myCell.toString();
                        }else if (colno==2){
                            C = myCell.toString();
                        }else if (colno==3){
                            D = myCell.toString();
                        }
                        colno++;
                        Log.e("", " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                    }// first cell while
                }//first cell if

                else if(rowno != 0) {
                    //그다음 컬럼이 있는지 확인해주는 메서드
                    Iterator<Cell> cellIter = myRow.cellIterator();
                    int colno =0;
                    String No="", Name="", Unit="",License="";
                    //컬럼 넘버가 있으면 실행
                    while (cellIter.hasNext()) {
                        HSSFCell myCell = (HSSFCell) cellIter.next();
                        if (colno==0){
                            No = myCell.toString();
                        }else if (colno==1){
                            Name = myCell.toString();
                        }else if (colno==2){
                            Unit = myCell.toString();
                        }else if (colno==3){
                            License = myCell.toString();
                            if(data.equals(myCell.toString())){
                                Log.e("", " Index :" + myCell.getColumnIndex() + " -- " + myCell.toString());
                                textView.append( "\n"+ A + " = " + No + "\n" + B + " = "  + Name + "\n" + C + " = "  + Unit + "\n" + D + " = "  + License +"\n");
                                exist = true;
                                break;
                            } }
                        colno++;
                    }// cell while
                }// cell if
                rowno++;
                if(exist == true){
                    break;
                }

            }// row while

            if(exist == false) {
                textView.append("엑셀에 데이터가 없습니다.");
            }

        } catch (Exception e) {
            Log.e("", "error "+ e.toString());
        }
    }// readExcelFileFromAssets
}
