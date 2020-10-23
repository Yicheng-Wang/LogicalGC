public class Utility {
    public static class Number{
        boolean endWithK = false;
        boolean isFloat = false;
        String size = "";
        int valueForm = 0;
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
            while(((num = rows.charAt(i)) != ' ' )&& num != ':'){
                size += num;
                i++;
            }
            result.size = size;
            result.endWithK = Number.judgeEnd(size);
            result.isFloat = Number.judgeFloat(size);
            result.completeAllForm();
            return result;
        }

        public static boolean judgeEnd(String input){
            return input.charAt(input.length() - 1) == 'K';
        }

        public static boolean judgeFloat(String input){
            return input.contains(".");
        }

        public void completeAllForm(){
            if(this.isFloat){
                this.valueDouble = Double.parseDouble(this.size);
            }
            else{
                int number;
                if(this.endWithK){
                    number= Integer.parseInt(this.size.substring(0,this.size.length()-2));
                    this.valueFormK = number;
                    this.valueForm = number << 10;
                    this.ValueFormM = (double)number / 1024;
                    this.valueFormG = this.ValueFormM / 1024;
                }
                else{
                    number= Integer.parseInt(this.size.substring(0,this.size.length()-1));
                    this.valueForm = number;
                    this.valueFormK = (double)number / 1024;
                    this.ValueFormM = this.valueFormK / 1024;
                    this.valueFormG = this.ValueFormM / 1024;
                }
            }
        }

    }

}
