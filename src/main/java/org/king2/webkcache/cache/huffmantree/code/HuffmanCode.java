package org.king2.webkcache.cache.huffmantree.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * =======================================================
 * 说明:  赫夫曼编码
 * <p>
 * 作者		时间					            注释
 *
 * @author 俞烨                                 创建
 * =======================================================
 */
public class HuffmanCode {

    /**
     * 赫夫曼树Code的缓冲流
     */
    private static StringBuilder SB = new StringBuilder();

    /**
     * 赫夫曼树的CodeMap集合
     */
    private static Map<Byte, String> dataCodes = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        createHuffmanCode("dasdas2313ralsdjokadjuauo123俞烨牛逼12312313sadaopdioadio dasdad");
        System.out.println(SB.toString());
    }

    /**
     * 构建一个赫夫曼编码
     */
    public static void createHuffmanCode(String data) {
        // 获取到需要构建的赫夫曼编码数据的字节数组
        byte[] dataBytes = data.getBytes();

        // 获取一颗权值树
        List<codeNode> huffmanWeight = createHuffmanWeight(dataBytes);

        // 通过权值构建一颗赫夫曼树
        codeNode huffManTreeByHuffManWeight = createHuffManTreeByHuffManWeight(huffmanWeight);

        // 通过一颗赫夫曼树得到他的赫夫曼编码
        buildHuffmanCodeBuilder(huffManTreeByHuffManWeight, "", SB);
    }


    /**
     * 通过字节数组转换成一颗Node节点的权值
     *
     * @return
     */
    private static List<codeNode> createHuffmanWeight(byte[] dataBytes) {

        // 统计这棵树共同出现过的字体个数
        Map<Byte, Integer> getByteSizeMaps = new ConcurrentHashMap<>();
        // 遍历二维数组
        for (byte dataByte : dataBytes) {
            Integer isEmpty = getByteSizeMaps.get(dataByte);
            if (isEmpty != null) {
                // 存在
                getByteSizeMaps.put(dataByte, isEmpty + 1);
            } else {
                // 不存在
                getByteSizeMaps.put(dataByte, 1);
            }
        }

        // 通过上面的方法 我们已经得出这个字节数组每个字节出现的次数
        // 遍历getByteSizeMaps并返回
        List<codeNode> nodes = new ArrayList<>();
        for (Map.Entry<Byte, Integer> entry : getByteSizeMaps.entrySet()) {
            nodes.add(new codeNode(entry.getKey(), entry.getValue()));
        }
        return nodes;
    }

    /**
     * 通过权值 构建一颗赫夫曼树
     *
     * @return
     */
    private static codeNode createHuffManTreeByHuffManWeight(List<codeNode> nodes) {

        while (nodes.size() > 1) {
            // 进行排序
            Collections.sort(nodes);
            // 取出两个最小的值 将他们的权值相加并重新存入nodes
            codeNode codeNode = nodes.get(0);
            codeNode codeNode1 = nodes.get(1);
            // 计算出他们父节点的值
            codeNode parentNode = new codeNode(null, codeNode.getWeight() + codeNode1.getWeight());
            parentNode.setLeft(codeNode);
            parentNode.setRight(codeNode1);
            // 重新存入nodes中
            nodes.add(parentNode);
            // 将使用过的数据删除
            nodes.remove(codeNode);
            nodes.remove(codeNode1);
        }

        return !nodes.isEmpty() ? nodes.get(0) : null;
    }


    /**
     * 通过赫夫曼树构建一个赫夫曼编码的缓冲流
     *
     * @param codeNode
     * @param code
     * @param stringBuilder
     */
    private static void buildHuffmanCodeBuilder(codeNode codeNode, String code, StringBuilder stringBuilder) {

        // 将上次的StringBuffer流赋值到新的StringBuffer
        StringBuilder stringBuilder1 = new StringBuilder(stringBuilder);
        stringBuilder1.append(code);

        // 判断节点是否为空
        if (codeNode != null) {
            if (codeNode.getData() == null) {
                // 说明是父节点 需要进行拼接
                buildHuffmanCodeBuilder(codeNode.getLeft(), "0", stringBuilder1);
                buildHuffmanCodeBuilder(codeNode.getRight(), "1", stringBuilder1);
            } else {
                // 说明已经到达叶子节点了 可以将数据存入Map中了
                dataCodes.put(codeNode.getData(), stringBuilder1.toString());
            }
        }

    }
}

class codeNode implements Comparable<codeNode> {

    // 本次节点的数据
    private Byte data;
    // 本次数据的权值
    private Integer weight;
    // 左子树和右子树
    private codeNode left;
    private codeNode right;

    public codeNode(Byte data, Integer weight) {
        this.data = data;
        this.weight = weight;
    }

    @Override
    public int compareTo(codeNode codeNode) {
        return this.weight - codeNode.weight;
    }

    public Byte getData() {
        return data;
    }

    public void setData(Byte data) {
        this.data = data;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public codeNode getLeft() {
        return left;
    }

    public void setLeft(codeNode left) {
        this.left = left;
    }

    public codeNode getRight() {
        return right;
    }

    public void setRight(codeNode right) {
        this.right = right;
    }
}
