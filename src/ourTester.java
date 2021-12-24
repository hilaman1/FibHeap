import java.util.ArrayList;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collections;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
public class ourTester {
    static final PrintStream stream = System.out;
    static Heap heap;
    static FibonacciHeap fibonacciHeap;
    static double grade;
    static double testScore;

    public static void main(String[] args) {
        try {
            test2();
        } catch (Exception e) {
            e.printStackTrace();
            bugFound("test2");

        }

    }

    static void test0() {
        String test = "test0";
        fibonacciHeap = new FibonacciHeap();

        ArrayList<Integer> numbers = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            numbers.add(i);
        }
        for (int i = 0; i < 7; i++) {
            fibonacciHeap.insert(numbers.get(i));
        }
        for (int i = 0; i < 7; i++) {
            if (fibonacciHeap.findMin().getKey() != i) {
                System.out.println("currMin is" + fibonacciHeap.findMin().getKey());
                bugFound(test);
                return;
            }
//            System.out.println("before delete "+i);
//            print(fibonacciHeap,true);
            fibonacciHeap.deleteMin();
//            System.out.println("after delete "+i);
//            print(fibonacciHeap,true);
        }


    }
    static void test2() {
        String test = "test2";
        heap = new Heap();
        fibonacciHeap = new FibonacciHeap();
        for (int i = 21; i >= 0; i--) {
            heap.insert(i);
            fibonacciHeap.insert(i);
        }
        while (!heap.isEmpty()) {
            if (heap.findMin() != fibonacciHeap.findMin().getKey() || heap.size() != fibonacciHeap.size()) {
                System.out.println("in heap min"+heap.findMin());
                System.out.println("in fibheap min"+fibonacciHeap.findMin().getKey());
                System.out.println("in heap size"+heap.size());
                System.out.println("in fibheap size"+fibonacciHeap.size());
                bugFound(test);
                return;
            }
            heap.deleteMin();
            fibonacciHeap.deleteMin();
        }
        if (!fibonacciHeap.isEmpty())
            bugFound(test);
    }

    static void bugFound(String test) {
        System.out.println("Bug found in " + test);
        grade -= testScore;
    }
    //todo-heapPrint method
    static void printIndentPrefix(ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        for (int i = 0; i < size - 1; ++i) {
            stream.format("%c   ", hasNexts.get(i).booleanValue() ? '│' : ' ');
        }
    }

    static void printIndent(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        printIndentPrefix(hasNexts);

        stream.format("%c── %s\n",
                hasNexts.get(size - 1) ? '├' : '╰',
                heapNode == null ? "(null)" : String.valueOf(heapNode.getKey())
        );
    }

    static String repeatString(String s,int count){
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < count; i++) {
            r.append(s);
        }
        return r.toString();
    }

    static void printIndentVerbose(FibonacciHeap.HeapNode heapNode, ArrayList<Boolean> hasNexts) {
        int size = hasNexts.size();
        if (heapNode == null) {
            printIndentPrefix(hasNexts);
            stream.format("%c── %s\n", hasNexts.get(size - 1) ? '├' : '╰', "(null)");
            return;
        }

        Function<Supplier<FibonacciHeap.HeapNode>, String> keyify = (f) -> {
            FibonacciHeap.HeapNode node = f.get();
            return node == null ? "(null)" : String.valueOf(node.getKey());
        };
        String title  = String.format(" Key: %d ", heapNode.getKey());
        List<String> content =  Arrays.asList(
                String.format(" Rank: %d ", heapNode.getRank()),
                String.format(" Marked: %b ", heapNode.isMarked()),
                String.format(" Parent: %s ", keyify.apply(heapNode::getParent)),
                String.format(" Next: %s ", keyify.apply(heapNode::getNext)),
                String.format(" Prev: %s ", keyify.apply(heapNode::getPrev)),
                String.format(" Child: %s", keyify.apply(heapNode::getChild))
        );

        /* Print details in box */
        int length = Math.max(
                title.length(),
                content.stream().map(String::length).max(Integer::compareTo).get()
        );
        String line = repeatString("─", length);
        String padded = String.format("%%-%ds", length);
        boolean hasNext = hasNexts.get(size - 1);

        //print header row
        printIndentPrefix(hasNexts);
        stream.format("%c── ╭%s╮%n", hasNext ? '├' : '╰', line);

        //print title row
        printIndentPrefix(hasNexts);
        stream.format("%c   │" + padded + "│%n", hasNext ? '│' : ' ', title);

        // print separator
        printIndentPrefix(hasNexts);
        stream.format("%c   ├%s┤%n", hasNext ? '│' : ' ', line);

        // print content
        for (String data : content) {
            printIndentPrefix(hasNexts);
            stream.format("%c   │" + padded + "│%n", hasNext ? '│' : ' ', data);
        }

        // print footer
        printIndentPrefix(hasNexts);
        stream.format("%c   ╰%s╯%n", hasNext ? '│' : ' ', line);
    }

    static void printHeapNode(FibonacciHeap.HeapNode heapNode, FibonacciHeap.HeapNode until, ArrayList<Boolean> hasNexts, boolean verbose) {
        if (heapNode == null || heapNode == until) {
            return;
        }
        hasNexts.set(
                hasNexts.size() - 1,
                heapNode.getNext() != null && heapNode.getNext() != heapNode && heapNode.getNext() != until
        );
        if (verbose) {
            printIndentVerbose(heapNode, hasNexts);
        } else {
            printIndent(heapNode, hasNexts);
        }

        hasNexts.add(false);
        printHeapNode(heapNode.getChild(), null, hasNexts, verbose);
        hasNexts.remove(hasNexts.size() - 1);

        until = until == null ? heapNode : until;
        printHeapNode(heapNode.getNext(), until, hasNexts, verbose);
    }

    public static void print(FibonacciHeap heap, boolean verbose) {
        if (heap == null) {
            stream.println("(null)");
            return;
        } else if (heap.isEmpty()) {
            stream.println("(empty)");
            return;
        }

        stream.println("╮");
        ArrayList<Boolean> list = new ArrayList<>();
        list.add(false);
        printHeapNode(heap.getFirst(), null, list, verbose);
    }

}
