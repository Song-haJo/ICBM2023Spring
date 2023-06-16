package com.example.emptyviewactivityspd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.emptyviewactivityspd.decorators.EventDecorator;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;



public class MainActivity extends AppCompatActivity {

    MaterialCalendarView materialCalendarView;
    //String[][] result = {{"2023,06,11","0","1","0","1","0","1"}};
    String[][] csvData;
    ToggleButton tbn1;
    ToggleButton tbn2;
    ToggleButton tbn3;
    ToggleButton tbn4;
    ToggleButton tbn5;
    ToggleButton tbn6;
    TextView txtView1;
    TextView txtView2;
    TextView txtView3;
    TextView txtView4;
    TextView txtView5;
    TextView txtView6;
    TextView txtView7;

    TextView target;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clearCSV();
        writeCSVft();
        csvData = readCSV();

        materialCalendarView = (MaterialCalendarView)findViewById(R.id.calendarView);
        tbn1 = (ToggleButton) findViewById(R.id.toggleButton);
        tbn2 = (ToggleButton) findViewById(R.id.toggleButton1);
        tbn3 = (ToggleButton) findViewById(R.id.toggleButton2);
        tbn4 = (ToggleButton) findViewById(R.id.toggleButton3);
        tbn5 = (ToggleButton) findViewById(R.id.toggleButton4);
        tbn6 = (ToggleButton) findViewById(R.id.toggleButton5);
        txtView1 = (TextView)findViewById(R.id.textView);
        txtView2 = (TextView)findViewById(R.id.textView1);
        txtView3 = (TextView)findViewById(R.id.textView2);
        txtView4 = (TextView)findViewById(R.id.textView3);
        txtView5 = (TextView)findViewById(R.id.textView4);
        txtView6 = (TextView)findViewById(R.id.textView5);
        txtView7 = (TextView)findViewById(R.id.textView6);

        showCSV();

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(2023, 0, 1)) // 달력의 시작
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 달력의 끝
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();


        String[] dateList = new String[csvData.length];
        for (int d = 0; d < csvData.length; d++) {
            dateList[d] = csvData[d][0];
        }

        new ApiSimulator(dateList).executeOnExecutor(Executors.newSingleThreadExecutor());

        /*
        // 선택된 날짜 출력
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                Log.i("Year test", Year + "");
                Log.i("Month test", Month + "");
                Log.i("Day test", Day + "");

                String shot_Day = Year + "," + Month + "," + Day;

                Log.i("shot_Day test", shot_Day + "");
                materialCalendarView.clearSelection();

                Toast.makeText(getApplicationContext(), shot_Day , Toast.LENGTH_SHORT).show();
            }
        });
        */


        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

                int Year = date.getYear();
                int Month = date.getMonth() + 1;
                int Day = date.getDay();

                String selectedDate = Year + "," + Month + "," + Day;

                int nth = isIn(csvData, 0, selectedDate);
                // 점 찍기
                if (nth < 0) {
                    csvData = Add(csvData, selectedDate);
                }
                //updateLength(selectedDate);

                // result[n][1~7]에 1이 있는 날짜에만 점 찍기 (X)
                // 사용자가 선택한 적 있는 날짜에 점 찍기 <- 선택을 안 한 것도 기록이므로
                String[] dateList = new String[csvData.length];
                for (int d = 0; d < csvData.length; d++) {
                    dateList[d] = csvData[d][0];
                }

                new ApiSimulator(dateList).executeOnExecutor(Executors.newSingleThreadExecutor());




                // 토글 버튼 상태 표시
                for (int sIndex = 0; sIndex < dateList.length; sIndex++) {
                    if (csvData[sIndex][0].equals(selectedDate)) {
                        //printText(Integer.toString(sIndex), 1);
                        //printText(Integer.toString(result.length), 2);
                        printText(csvData[sIndex][1], 1);
                        printText(csvData[sIndex][2], 2);
                        printText(csvData[sIndex][3], 3);
                        printText(csvData[sIndex][4], 4);
                        printText(csvData[sIndex][5], 5);
                        printText(csvData[sIndex][6], 6);
                        //printText(result[sIndex][0], 7);

                    }
                }
                //updateLength(result[dateIndex-1][1]);

                if (tbn1.performClick()) UpdateStat(csvData, nth);
                if (tbn2.performClick()) UpdateStat(csvData, nth);
                if (tbn3.performClick()) UpdateStat(csvData, nth);
                if (tbn4.performClick()) UpdateStat(csvData, nth);
                if (tbn5.performClick()) UpdateStat(csvData, nth);
                if (tbn6.performClick()) UpdateStat(csvData, nth);

                showToggle(nth);

            }
        });
    }

    // Read the csv file and add the values into 2D string array.
    private String[][] readCSV() {
        // Get the absolute path of the file
        String filePath = getFilesDir().getAbsolutePath() + File.separator + "SelfDiagnosis.csv";

        // List to store the CSV data
        List<String[]> csvData = new ArrayList<>();

        // Read the CSV file using OpenCSV
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            while ((line = reader.readNext()) != null) {
                csvData.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }

        // Convert the list to a 2D array
        String[][] dataArray = new String[csvData.size()][];
        for (int i = 0; i < csvData.size(); i++) {
            dataArray[i] = csvData.get(i);
        }

        return dataArray;
    }

    // Show the data in the csv file on txtView7.
    private void showCSV() {
        StringBuilder contentBuilder = new StringBuilder();

        // Get the CSV data
        String[][] csvData = readCSV();

        // Build the formatted content from the 2D array
        for (String[] row : csvData) {
            // Join the elements of the row with a comma delimiter
            String rowString = String.join(",", row);
            // Append the row string to the output with a line break
            contentBuilder.append(rowString).append("\n");
        }

        // Set the formatted content to txtView7
        txtView7.setText(contentBuilder.toString());
    }


    // Add sample values for the first time.
    private void writeCSVft() {
        // Your modified data to be written to the CSV file
        List<String[]> modifiedData = new ArrayList<>();
        modifiedData.add(new String[]{"2023,06,13", "1", "0", "1", "0", "1", "1"});
        modifiedData.add(new String[]{"2023,06,18", "1", "1", "0", "1", "0", "1"});
        modifiedData.add(new String[]{"2023,06,10", "0", "0", "1", "1", "1", "0"});

        // Get the path to the internal storage directory
        String internalStoragePath = getFilesDir().getAbsolutePath();

        // Specify the file name and path in the internal storage
        String csvFilePath = internalStoragePath + File.separator + "SelfDiagnosis.csv";

        // Write modified data to the CSV file in internal storage
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(csvFilePath, true));
            for (String[] data : modifiedData) {
                StringBuilder lineBuilder = new StringBuilder();
                for (String value : data) {
                    lineBuilder.append('"').append(value).append('"').append(",");
                }
                lineBuilder.deleteCharAt(lineBuilder.length() - 1);  // Remove the trailing comma
                printWriter.println(lineBuilder.toString());
            }
            printWriter.close();

            // Show a success message
            Toast.makeText(this, "Data written to CSV file", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void add2CSV(String[][] csvData, String[] newValue) {
        // Get the path to the internal storage directory
        String internalStoragePath = getFilesDir().getAbsolutePath();

        // Specify the file name and path in the internal storage
        String csvFilePath = internalStoragePath + File.separator + "SelfDiagnosis.csv";

        // Check if the new value already exists in the CSV data
        for (String[] data : csvData) {
            if (Arrays.equals(data, newValue)) {
                // Value already exists, return without adding it again
                Toast.makeText(this, "Value already exists in CSV file", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Write modified data to the CSV file in internal storage
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(csvFilePath, true));
            StringBuilder lineBuilder = new StringBuilder();
            for (String value : newValue) {
                lineBuilder.append('"').append(value).append('"').append(",");
            }
            lineBuilder.deleteCharAt(lineBuilder.length() - 1);  // Remove the trailing comma
            printWriter.println(lineBuilder.toString());
            printWriter.close();

            // Show a success message
            Toast.makeText(this, "Data written to CSV file", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    // Delete all data in the csv file.
    private void clearCSV() {
        // Get the absolute path of the file
        String filePath = getFilesDir().getAbsolutePath() + File.separator + "SelfDiagnosis.csv";

        // Create the file object
        File csvFile = new File(filePath);

        // Check if the file exists
        if (csvFile.exists()) {
            // Delete the file
            boolean deleted = csvFile.delete();

            if (deleted) {
                // Create a new empty file
                try {
                    boolean created = csvFile.createNewFile();
                    if (created) {
                        Toast.makeText(this, "CSV file cleared", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to clear CSV file", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to clear CSV file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to clear CSV file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "CSV file not found", Toast.LENGTH_SHORT).show();
        }
    }

    // Check if there is the value in the array.
    private int isIn(String[][] array, int nthVal, String val) {
        int n = -1;
        for (int sIndex = 0; sIndex < array.length; sIndex++) {
            if (csvData[sIndex][nthVal].equals(val)) {
                n = sIndex;
            }
        }
        return n;
    }

    // Add the item into the array.
    private String[][] Add(String[][] originArr, String val) {
        // (원본 배열의 크기 + 1)를 크기를 가지는 배열을 생성
        String[][] newArray = new String[originArr.length + 1][7];

        // 새로운 배열에 값을 순차적으로 할당
        for (int index = 0; index < originArr.length; index++) {
            System.arraycopy(originArr[index], 0, newArray[index], 0, 7);
        }

        // 새로운 배열의 마지막 위치에 추가하려는 값을 할당
        newArray[originArr.length][0] = val;

        if (tbn1.isChecked() == true) {
            newArray[originArr.length][1] = "1";
        } else {
            newArray[originArr.length][1] = "0";
        }
        if (tbn2.isChecked() == true) {
            newArray[originArr.length][2] = "1";
        } else {
            newArray[originArr.length][2] = "0";
        }
        if (tbn3.isChecked() == true) {
            newArray[originArr.length][3] = "1";
        } else {
            newArray[originArr.length][3] = "0";
        }
        if (tbn4.isChecked() == true) {
            newArray[originArr.length][4] = "1";
        } else {
            newArray[originArr.length][4] = "0";
        }
        if (tbn5.isChecked() == true) {
            newArray[originArr.length][5] = "1";
        } else {
            newArray[originArr.length][5] = "0";
        }
        if (tbn6.isChecked() == true) {
            newArray[originArr.length][6] = "1";
        } else {
            newArray[originArr.length][6] = "0";
        }

        add2CSV(csvData, newArray[newArray.length-1]);
        // 새로운 배열을 반환
        return newArray;
    }

    // Return the length of the array.
    private void printText(String input, int nth) {
        if (nth == 1) {
            target = txtView1;
        } else if (nth == 2) {
            target = txtView2;
        } else if (nth == 3) {
            target = txtView3;
        } else if (nth == 4) {
            target = txtView4;
        } else if (nth == 5) {
            target = txtView5;
        } else if (nth == 6) {
            target = txtView6;
        } else if (nth == 7) {
            target = txtView7;
        }

        Log.d(this.getClass().getName(), (String)target.getText());
        //String temp = Integer.toString(result.length);
        //String temp = result.toString();
        target.setText(input);
    }

    public void showToggle(int dateIndex) {
        if (dateIndex < 0) {
            tbn1.setChecked(false);
            tbn2.setChecked(false);
            tbn3.setChecked(false);
            tbn4.setChecked(false);
            tbn5.setChecked(false);
            tbn6.setChecked(false);
        } else {
            if (csvData[dateIndex][1].equals("1")) {
                tbn1.setChecked(true);
            } else {
                tbn1.setChecked(false);
            }
            if (csvData[dateIndex][2].equals("1")) {
                tbn2.setChecked(true);
            } else {
                tbn2.setChecked(false);
            }
            if (csvData[dateIndex][3].equals("1")) {
                tbn3.setChecked(true);
            } else {
                tbn3.setChecked(false);
            }
            if (csvData[dateIndex][4].equals("1")) {
                tbn4.setChecked(true);
            } else {
                tbn4.setChecked(false);
            }
            if (csvData[dateIndex][5].equals("1")) {
                tbn5.setChecked(true);
            } else {
                tbn5.setChecked(false);
            }
            if (csvData[dateIndex][6].equals("1")) {
                tbn6.setChecked(true);
            } else {
                tbn6.setChecked(false);
            }
        }
    }

    // Update the array.
    public String[][] UpdateStat(String[][] originArr, int nth) {
        // (원본 배열의 크기 + 1)를 크기를 가지는 배열을 생성
        String[][] newArray = new String[originArr.length][7];

        // 새로운 배열에 값을 순차적으로 할당
        for (int index = 0; index < originArr.length; index++) {
            System.arraycopy(originArr[index], 0, newArray[index], 0, 7);
        }


        if (tbn1.isChecked() == true) {
            newArray[nth][1] = "1";
        } else {
            newArray[nth][1] = "0";
        }
        if (tbn2.isChecked() == true) {
            newArray[nth][2] = "1";
        } else {
            newArray[nth][2] = "0";
        }
        if (tbn3.isChecked() == true) {
            newArray[nth][3] = "1";
        } else {
            newArray[nth][3] = "0";
        }
        if (tbn4.isChecked() == true) {
            newArray[nth][4] = "1";
        } else {
            newArray[nth][4] = "0";
        }
        if (tbn5.isChecked() == true) {
            newArray[nth][5] = "1";
        } else {
            newArray[nth][5] = "0";
        }
        if (tbn6.isChecked() == true) {
            newArray[nth][6] = "1";
        } else {
            newArray[nth][6] = "0";
        }

        // 새로운 배열을 반환
        return newArray;
    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        String[] Time_Result;

        ApiSimulator(String[] Time_Result){
            this.Time_Result = Time_Result;
        }

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {
            Calendar calendar = Calendar.getInstance();
            ArrayList<CalendarDay> dates = new ArrayList<>();

            /*
            특정 날짜 달력에 점 표시
            월은 0이 1월
            년, 일은 그대로
            string 문자열인 Time_Result를 받아와서 ','를 기준으로 자르고 string을 int로 변환
            */

            for(int i = 0 ; i < Time_Result.length ; i ++){
                CalendarDay day = CalendarDay.from(calendar.getTime());
                String[] time = Time_Result[i].split(",");
                int year = Integer.parseInt(time[0]);
                int month = Integer.parseInt(time[1]);
                int date = Integer.parseInt(time[2]);

                dates.add(day);
                calendar.set(year,month-1, date);
            }

            return dates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

            if (isFinishing()) {
                return;
            }

            materialCalendarView.addDecorator(new EventDecorator(Color.RED, calendarDays,MainActivity.this));
        }


    }


}
