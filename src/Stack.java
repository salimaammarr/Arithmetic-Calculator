
public class Stack<E> {
    private E[] array;
    private int top=-1;
    public Stack(int capacity){
        array = (E[]) new Object[capacity];
    }
    public boolean isEmpty(){
        return top==-1;
    }
    public E pop() {
        if (isEmpty()){
            System.out.println("Stack is empty");
            return null;
        }
        E temp = array[top];
        array[top] = null;
        top--;
        return temp;
    }
    public void push(E element){
        if(top==array.length-1){
            expandArray();
        }
        array[++top] = element;
    }
    public void expandArray(){
        E[] expandedArray= (E[]) new Object[array.length*2];
        for(int i=0;i<array.length;i++){
            expandedArray[i] = array[i];
        }
        array = expandedArray;
    }
    public E peek() {
        if (isEmpty()) {
            return null;
        }
        return array[top];
    }

}
