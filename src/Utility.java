public class Utility {
    public static class Number{
        boolean endWithK = false;
        boolean isFloat = false;
        String size = "";
        public static Number parseNumber (String Symbol, String rows){
            int sizeStartIndex = rows.indexOf(Symbol);
            int i = sizeStartIndex + Symbol.length();
            Number result = new Number();
            String size = "";
            char num;
            while((num = rows.charAt(i)) != ' '){
                size += num;
                i++;
            }
            result.size = size;
            result.endWithK = Number.judgeEnd(size);
            return result;
        }

        public static boolean judgeEnd(String input){
            return (input.charAt(input.length()-1) == 'K')?true:false;
        }

        public static boolean judgeFloat(String input){
            return (input.contains("."))?true:false;
        }
    }

}
