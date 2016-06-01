package com.example.zzphoneguard.utils;

/**
 * Created by 狗蛋儿 on 2016/6/1.
 * 双链表
 */
public class DoubleLink {
    private class Node{
        Object data = new Object();
        Node(Object data){
            this.data=data;
        }
        Node prev;
        Node next;
    }
    private Node head;//头节点
    private Node tail;//尾节点

    /**
     *
     * @param data
     *      添加数据
     */
    public void add(Object data){
        Node node = new Node(data);
        if (head==null){
            //链表为空
            head=node;
            tail=node;
        }else {
            //链表不为空，从链表的尾部添加数据
            tail.next=node;//老的尾节点的next指向新添加的节点
            node.prev=tail;//新的尾节点的prev指向老的尾节点
            tail=node;//新添加的节点成为新的尾节点
        }
    }

    /**
     *
     * @param data
     *      删除数据
     */
    public void remove(Object data){
        Node temp = find(data);
        if (temp!=null){
            if (head==temp&&tail==temp){
                //链表中只有一个节点
                head=null;
                tail=null;
            }else if (head==temp){
                //删除头节点
                head = head.next;//第二个节点成为新的头节点
                head.prev = null;//断开新的头节点的prev
            }else if (tail==temp){
                //删除尾节点
                tail = tail.prev;//倒数第二个节点成为新的尾节点
                tail.next = null;//断开新的尾节点的next
            }else{
                //删除中间节点
                temp.prev.next=temp.next;
                temp.next.prev=temp.prev;
            }
        }
    }

    /**
     *
     * @param data
     * 输入要寻找的数据
     * @return
     * 找到的链表中的数据
     */
    private Node find(Object data){
        Node temp = head;
        while (temp!=null){
            if (temp.data.equals(data)&&temp.hashCode()==data.hashCode()){
                break;
            }
            temp=temp.next;//
        }
        return temp;
    }

}
