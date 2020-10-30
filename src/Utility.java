import java.lang.reflect.Field;

public class Utility {

    public static String parseString(String Symbol, String rows){
        int sizeStartIndex = rows.indexOf(Symbol);
        int i = sizeStartIndex + Symbol.length();
        String result = "";
        char word;
        while((word = rows.charAt(i)) != ')' ){
            result += word;
            i++;
        }
        return result;
    }

    public static Object[] skipSpace(String row, Integer cursor){
        String content = "";
        while(row.charAt(cursor) == ' ')
            cursor++;
        char num;
        while(cursor < row.length() && (num = row.charAt(cursor) )!= ' ' ){
            content += num;
            cursor++;
        }
        int cursorvalue = cursor;

        try{
            Field field = Integer.class.getDeclaredField("value");
            field.setAccessible(true);
            field.set(cursor, cursorvalue);
        }catch (Exception e){
            e.printStackTrace();
        }
        Object[] back = new Object[]{content,cursor};
        return back;
    }

    public static class Number{
        boolean endWithK = false;
        boolean isFloat = false;
        boolean isPercentage = false;
        String size = "";
        long valueForm = 0;
        double valueDouble = 0.0;
        double valueFormK = 0;
        double ValueFormM = 0;
        double valueFormG = 0;
        public static Number parseNumber (String Symbol, String rows){
            int sizeStartIndex = rows.indexOf(Symbol);
            int i = sizeStartIndex + Symbol.length();
            Number result = new Number();
            String size = "";
            char num;
            while(((num = rows.charAt(i)) != ' ' )&& num != ':' && num != ',' && num != '-' && num != '('){
                size += num;
                i++;
            }
            result.size = size;
            result.endWithK = Number.judgeEnd(size);
            result.isFloat = Number.judgeFloat(size);
            result.isPercentage = Number.judgePercentage(size);
            result.completeAllForm();
            return result;
        }

        public static boolean judgeEnd(String input){
            return input.charAt(input.length() - 1) == 'K';
        }

        public static boolean judgeFloat(String input){
            return input.contains(".");
        }

        public static boolean judgePercentage(String input){
            return input.contains("%");
        }

        public static Number dealingPercentage(Number per,Number value){
            per.size = per.size.substring(0,per.size.length()-1);
            per.valueForm = value.valueForm * Integer.parseInt(per.size) / 100;
            per.size = Long.toString(per.valueForm);
            per.isPercentage = false;
            per.completeAllForm();
            return per;
        }

        public void completeAllForm(){
            if(this.isPercentage){
                return;
            }
            if(this.isFloat){
                this.valueDouble = Double.parseDouble(this.size);
            }
            else{
                long number;
                if(this.endWithK){
                    number= Long.parseLong(this.size.substring(0,this.size.length()-1));
                    this.valueFormK = number;
                    this.valueForm = number << 10;
                    this.ValueFormM = (double)number / 1024;
                    this.valueFormG = this.ValueFormM / 1024;
                }
                else{
                    number= Long.parseLong(this.size);
                    this.valueForm = number;
                    this.valueFormK = (double)number / 1024;
                    this.ValueFormM = this.valueFormK / 1024;
                    this.valueFormG = this.ValueFormM / 1024;
                }
            }
        }

    }

}
