package utilclass;
import java.util.ArrayList;
import java.util.List;

public class ListUtil {

    /**
     * 按指定大小，分隔集合，将集合按规定个数分为n个部分
     * List<int> 按照5个一组将原始List分割形成List<List<int>>形式
     * @param list
     * @param len
     * @return
     */
    public static List<List<?>> splitList(List<?> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }

        List<List<?>> result = new ArrayList<List<?>>();


        int size = list.size();
        int count = (size + len - 1) / len;


        for (int i = 0; i < count; i++) {
            List<?> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

    public static void main(String[] args) {
        List<Integer> arr = new ArrayList<>();
        for(int i=0;i<10003;i++){
            arr.add(i);
        }
        for(List<?> list: splitList(arr, 1000)){
            System.out.println(list);
        }

    }

}
