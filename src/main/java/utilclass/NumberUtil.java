package utilclass;
import java.util.HashSet;
import java.util.Set;

/**
 * 获取随机数
 * fanddong
 */
public class NumberUtil {
    /**
     * 生成指定范围内的随机数[a, b)
     *
     * @param min 最小值（包括）
     * @param max 最大值（不包括）
     * @param n   随机数个数
     *            返回Object[]
     */
    public static Object[] randomCommon1(int min, int max, int n) {
        if (n >= (max - min + 1) || max < min) {
            return null;
        }
        Set set = new HashSet();
        while (set.size() < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            set.add(num);
        }
        return set.toArray();
    }

    public static void main(String[] args) {
        Object[] arr = randomCommon1(0, 3, 4);
        if (arr == null)
            System.out.println("不符合要求");
        for (Object i : arr) {
            System.out.println(i);
        }

    }
}
