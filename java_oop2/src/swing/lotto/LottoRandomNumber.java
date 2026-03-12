package swing.lotto;

public class LottoRandomNumber {
    int[] arr = new int[45];

    public LottoRandomNumber(){
        for (int i = 0; i < arr.length; i++){
            arr[i] = i + 1;
        }
        arr = suffleNumber(arr);
        arr = selectNumber(arr);
    }

    public int[] selectNumber(int[] arr){
        int[] result = new int[6];

        for (int i = 0; i < result.length; i++){
            result[i] = arr[i];
        }

        return result;
    }

    public int[] suffleNumber(int[] arr){
        for(int i = 0; i < arr.length; i++){
            for(int j = 0; j < arr.length; j++){
                if(Math.random() > 0.5){
                    int num = arr[i];
                    arr[i] = arr[j];
                    arr[j] = num;
                }
            }
        }

        return arr;
    }
}
