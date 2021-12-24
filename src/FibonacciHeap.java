//todo-delete import- added to make heapPrint work
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * FibonacciHeap
 *
 * An implementation of a Fibonacci Heap over integers.
 */
public class FibonacciHeap
{
    private HeapNode minNode;
    private HeapNode head; // circular array
    private HeapNode tail; // circular array
    private int size;
    public static int totalCuts = 0;
    public static int totalLinks = 0;
    public int markedNodesCnt = 0;
    public int treesCnt = 0;
    //todo-delete stream- added to make heapPrint work
    static final PrintStream stream = System.out;

    public FibonacciHeap(){
        // todo-think about constructors
        this.size=0;
        this.minNode =null;
        this.head =null;
        this.tail =null;
        // todo-potential is 0 when creating a new FibHeap
//        markedNodesCnt = 0 and treesCnt = 0, that's what defines the potential so it's 0
    }
    /**
     * public boolean isEmpty()
     *
     * Returns true if and only if the heap is empty.
     *
     */
    public boolean isEmpty()
    {
        return (this.size == 0);
    }

    /**
     * public HeapNode insert(int key)
     *
     * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
     * The added key is assumed not to already belong to the heap.
     *
     * Returns the newly created node.
     */
    public HeapNode insert(int key)
    {
        HeapNode nodeToInsert;
        if (isEmpty()){
            nodeToInsert = new HeapNode(key); // create a B_0 tree with key
            // TODO: 21/12/2021 check if needs to make a method
            nodeToInsert.setNext(nodeToInsert);
            nodeToInsert.setPrev(nodeToInsert);
            this.head = nodeToInsert;
            this.tail = nodeToInsert;
            this.minNode = nodeToInsert;
        }else{
            nodeToInsert = new HeapNode(key, this.tail, this.head);
//            update prev and next of current head and tail
            this.head.setPrev(nodeToInsert);
            this.tail.setNext(nodeToInsert);
            this.head = nodeToInsert; // create a B_0 tree, parent is null, no child and next is head and prev is tail
            if (nodeToInsert.getKey() < this.minNode.getKey()){
                this.minNode = nodeToInsert; // update minNode if needed
            }
        }
        this.size++; //added a node
        this.treesCnt++; // added a tree
        return nodeToInsert;
    }

    /**
     * public void deleteMin()
     *
     * Deletes the node containing the minimum key.
     *
     */
    public void deleteMin() {
        if (!this.isEmpty()) {
            if (this.minNode.getRank() != 0) { // minNode have sons
                if (this.minNode.getNext() != this.minNode){
                    if (this.minNode == this.head){
//                        left child of node to delete is new head
                        updateChildMark();
                        this.head = this.minNode.getChild();
                        this.minNode.getChild().setPrev(tail);
                        this.tail.setNext(this.minNode.getChild());

                        HeapNode lastChild = findLastChild();

                        this.minNode.getNext().setPrev(lastChild);
                        lastChild.setNext(this.minNode.getNext());
                        this.treesCnt = this.treesCnt - 1 + this.minNode.getRank();
                        consolidation();
                        this.size --;
                    }else if (this.minNode == this.tail) {
                        updateChildMark();
                        this.minNode.getChild().setPrev(this.minNode.getPrev());
                        this.minNode.getPrev().setNext(this.minNode.getChild());

                        HeapNode lastChild = findLastChild();

                        this.tail = lastChild;
                        lastChild.setNext(this.head);
                        this.head.setPrev(lastChild);
                        this.treesCnt = this.treesCnt - 1 + this.minNode.getRank();
                        consolidation();
                        this.size --;
                    }
                }else{ // head = tail
                    updateChildMark();
                    this.head = this.minNode.getChild();
                    this.tail = findLastChild();
                    this.treesCnt = this.treesCnt - 1 + this.minNode.getRank();
                    consolidation();
                    size--;
                }
//                updateRoots(minHeapNode);
//                removeMin(minHeapNode);
//                if (minHeapNode == minHeapNode.getNext()) {
//                    this.minNode = null;
//                } else {
//                    this.minNode = minHeapNode.getNext();
//                    consolidation();
//                }
//                this.size = this.size - 1;
            }else{ // minNode doesn't have sons
                if (this.size == 1){ // minNode is alone in heap
                    initializeFields();
//                  reset Heap
                }else {
//                    update head or tail if needed
                    if (this.minNode == this.head) {
                        this.head = this.minNode.getNext();
                    }
                    if (this.minNode == this.tail) {
                        this.tail = this.minNode.getNext();
                    }
                    this.minNode.getPrev().setNext(this.minNode.getNext());
                    this.minNode.getNext().setPrev(this.minNode.getPrev());
                    this.size--;
                    this.treesCnt--;
                    consolidation();
                }
            }
        }
    }

    private HeapNode findLastChild() {
        HeapNode lastChild = this.minNode.getChild();
        HeapNode firstChild = this.minNode.getChild();
//                        find last child of minNode
        for (int i = 2; i <= this.minNode.rank; i++) {
            firstChild = firstChild.getNext();
            if (firstChild.isMarked()){
                markedNodesCnt--;
            }
            firstChild.setMarked(false);
            firstChild.setParent(null);
            lastChild = firstChild;
        }
        return lastChild;
    }

    private void updateChildMark() {
        if (this.minNode.getChild().isMarked()){ // update mark after deleting child
            markedNodesCnt--;
            this.minNode.getChild().setMarked(false);
            this.minNode.getChild().setParent(null);
        }
    }

    private void removeMin(HeapNode minNode) {
        //at least one heapNode
        if (this.size() == 1) {
            //only minNode in heap
            initializeFields();
        } else { //size>1
            if (minNode.getChild() == null) {
                removeHeapNode(this.minNode);
            } else {
                //minNode has at least one Child
                if (this.minNode == this.head) {
                    HeapNode lastChild=this.minNode.getChild().getPrev();
                    this.head=this.minNode.getChild();
                    lastChild.setNext(this.minNode.getNext());
                    this.minNode.getNext().setPrev(lastChild);
                    this.head.setPrev(this.tail);
                    this.tail.setNext(this.head);

                } else if (this.minNode == this.tail) {
                    HeapNode prevHN=this.minNode.getPrev();
                    HeapNode lastChild=this.minNode.getChild().getPrev();
                    prevHN.setNext(this.minNode.getChild());
                    this.minNode.getChild().setPrev(prevHN);
                    this.head.setPrev(lastChild);
                } else { // this min node is in between && has at least one child
                    this.minNode.getChild().getPrev().setNext(this.minNode.getNext()); // x_k -> y_3
                    this.minNode.getNext().setPrev(this.minNode.getChild().getPrev()); // y_3 -> x_k
                    this.minNode.getChild().setPrev(this.minNode.getPrev()); // x_1 -> y_1
                    this.minNode.getPrev().setNext(this.minNode.getChild()); // y_1 -> x_1

                }
            }

        }
    }

    private void consolidation() {
        // successive linking- creates a valid Binomial Heap from FibHeap
        HeapNode [] rankArray = new HeapNode[(int)(Math.log(this.size) / Math.log(2)) + 1]; // there are log_2(n) trees at most
        HeapNode currRoot = this.head;
        rankArray[currRoot.getRank()] = currRoot;
        HeapNode nextRoot = currRoot.getNext();
        currRoot.setPrev(null);
        currRoot.setNext(null);

        while (this.head != nextRoot){
            if (rankArray[nextRoot.getRank()] == null) { // no need to link
                rankArray[nextRoot.getRank()] = nextRoot;
                currRoot = nextRoot;
                nextRoot = nextRoot.getNext();
                currRoot.setNext(null);
                currRoot.setPrev(null);
            }else {
                // there is a tree with the same rank as currRoot
                // we should link them
                boolean continueLinking = true;
                currRoot = nextRoot;
                nextRoot = nextRoot.getNext();
                currRoot.setNext(null);
                currRoot.setPrev(null);
                HeapNode rootToLink = rankArray[currRoot.getRank()];
                rankArray[currRoot.getRank()] = null;
                while (continueLinking){
//                        in case that we link between B_x with B_x and create B_(x+1) and B_(x+1) is null
                    if (rankArray[currRoot.getRank() + 1] == null){
                        //after linking two trees with rank i ==> currently  no tree in rank i.
                        rankArray[currRoot.getRank()] = null;
                        // move tree to the index of rank after link
                        rankArray[currRoot.getRank() + 1] = link(currRoot,rootToLink);
//                        no need to link anymore
                        continueLinking = false;
                    }
                    else{
                        currRoot = link(currRoot,rootToLink);
                        rootToLink = rankArray[currRoot.getRank()];
                        rankArray[currRoot.getRank()] = null; //after linking two trees with rank i ==> currently  no tree in rank i.
                    }
                }
            }
        }

        // now we should make sure the roots "list" is ordered by increasing rank
        //so we will collect trees with higher rank first and assign it as the last

        this.head = null;
        this.tail = null;
        this.minNode = null;

        HeapNode pos = null;

        for(int i = 0; i <rankArray.length; i++){
            if(rankArray[i] != null){
                if (this.isEmpty()){
                    //the heap is empty
                    HeapNode firstTree=rankArray[i];
                    firstTree.setNext(firstTree);
                    firstTree.setPrev(firstTree);
                    this.tail=firstTree;
                    this.head =firstTree;
                    this.minNode=firstTree;
                    pos = this.head;
                    //todo-think about a method to initialze new "heap"
                } else{
                    //roots list isnt empty-we should update pointers
                    // "inserting" currTree into the list of roots.
                    //for each nonempty index theres a tree with its rank so rootsNum
                    // so treesCnt should get increased by 1
                    // TODO: 24/12/2021  assert pos != null;
                    pos.setNext(rankArray[i]);
                    rankArray[i].setPrev(pos);
                    this.head.setPrev(rankArray[i]);
                    rankArray[i].setNext(this.head);
                    this.tail =rankArray[i]; //tail is updated though out the loop
                    //maintain the minNode
                    if(rankArray[i].getKey() < this.minNode.getKey()){
                        this.minNode=rankArray[i];
                    }
                    pos = rankArray[i];
                }
            }
        }
    }

    private void initializeFields() {
        //sets all fields to their default value
        this.minNode =null;
        this.treesCnt=0;
        this.size=0;
        this.markedNodesCnt=0;
        this.head=null;
        this.tail=null;
    }
    private void insertAfter(HeapNode currNode, HeapNode nextNode) {
        nextNode.setPrev(currNode);
        nextNode.setNext(currNode.getNext());
        currNode.setNext(nextNode);
        nextNode.getNext().setPrev(nextNode);
    }

    private HeapNode link(HeapNode currRoot, HeapNode rootToLink) {
        //link bigger to smaller
        //remove heapNode from rootsList
        if (currRoot.getKey() < rootToLink.getKey()){ // currRoot is smaller
            if (currRoot.getRank() > 0){ // currRoot has sons
                currRoot.setChild(rootToLink);
                rootToLink.setParent(currRoot);
                rootToLink.setNext(currRoot.getChild());
                rootToLink.setPrev(currRoot.getChild().getPrev());
                currRoot.getChild().getPrev().setNext(rootToLink);
                currRoot.getChild().setPrev(rootToLink);
            }else {
                currRoot.setChild(rootToLink);
                rootToLink.setParent(currRoot);
                rootToLink.setNext(rootToLink);
                rootToLink.setPrev(rootToLink);
            }
            currRoot.setRank((currRoot.getRank())+1); // cause we added a son
            this.treesCnt--;
//            link done
            totalLinks++;
            return currRoot;
        }else { // currRoot is bigger
            if (rootToLink.getRank() > 0){ // currRoot has sons
                HeapNode R2LChild = rootToLink.getChild();
                rootToLink.setChild(currRoot);
                currRoot.setParent(rootToLink);
                currRoot.setNext(R2LChild);
                currRoot.setPrev(R2LChild.getPrev());
                R2LChild.getPrev().setNext(rootToLink);
                R2LChild.setPrev(rootToLink);
            }else {
                rootToLink.setChild(currRoot);
                currRoot.setParent(rootToLink);
                currRoot.setNext(currRoot);
                currRoot.setPrev(currRoot);
            }
            rootToLink.setRank((rootToLink.getRank())+1); // cause we added a son
            this.treesCnt--;
//            link done
            totalLinks++;
            return currRoot;
        }
    }

    private void insertHeapNodeAsFirst(HeapNode smaller, HeapNode bigger) {

        if (smaller.getChild()==null){
            //smaller has no children- B_0
            bigger.setNext(bigger);
            bigger.setPrev(bigger);
            smaller.setChild(bigger);
            bigger.setParent(smaller);
        }
        else {//smaller has at least one child
            HeapNode last=smaller.getChild().getPrev();
            HeapNode second=smaller.getChild();
            bigger.setNext(second);
            second.setPrev(bigger);
            bigger.setPrev(last);
            last.setNext(bigger);
            bigger.setParent(smaller);
            smaller.setChild(bigger);
            smaller.setRank(smaller.getRank()+1);
        }
    }

    private void removeHeapNode(HeapNode bigger) {
        //this method removes a given heap node from the list of roots
        // @pre: bigger is in the heap
        if (bigger==this.head){
            //bigger is the head of rootsList
            this.head=this.head.getNext();
            this.tail.setNext(this.head);
            this.head.setPrev(this.tail);
//            print(this,true);
        }
        else{
            //bigger is not the head
            HeapNode prevHN=bigger.getPrev();
            prevHN.setNext(prevHN.getNext().getNext());
            if (prevHN.getNext().getNext()!=null){
                prevHN.getNext().getNext().setPrev(prevHN);
            }
            if (bigger==this.tail){
                //bigger is the last tree in rootsList
                this.tail=prevHN;
//                print(this,true);
            }
        }
        this.treesCnt-=1;

    }

    private void updateRoots(HeapNode minHeapNode){
        if (minHeapNode!=null){
            HeapNode currChild=this.minNode.getChild();
            while (currChild!=null){
                currChild.setParent(null);
                currChild.setMarked(false); // becomes a root - root is never marked
                this.treesCnt+=1;
                currChild=currChild.getNext();
            }
        }

    }

    /**
     * public HeapNode findMin()
     *
     * Returns the node of the heap whose key is minimal, or null if the heap is empty.
     *
     */
    public HeapNode findMin()
    {
        if (this.isEmpty()){
            return null;
        }
        return this.minNode;
    }

    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Melds heap2 with the current heap.
     *
     */
    public void meld (FibonacciHeap heap2)
    {
        if (this.isEmpty()&&!heap2.isEmpty()){
            //this heap is empty- should update rootsList to be heap2s' rootsList
            this.head=heap2.head;
            this.tail=heap2.tail;
            this.size = heap2.size();
            this.minNode = heap2.minNode;
//            this.head.setPrev(this.tail);
//            this.tail.setNext(this.head);
            //number of trees doesnt change-potential doesnt change

        }
        if(!this.isEmpty()&& !heap2.isEmpty()){
            //if !this.isEmpty()&& heap2.isEmpty() - nothing to do
            // next case we have to deal with- both not empty
            this.tail.setNext(heap2.head);
            heap2.head.setPrev(this.tail);
            this.head.setPrev(heap2.tail);
            this.tail.setNext(this.head);
            this.tail=heap2.tail;

            //maintain minHeapNode
            // TODO: 24/12/2021 maybe check: (this.minNode==null) ||(heap2.minNode!=null)&&
            if( heap2.minNode.getKey()<this.minNode.getKey()){
                this.minNode=heap2.minNode;
            }
            //maintain other fields
            this.size += heap2.size();
            this.treesCnt += heap2.treesCnt;
//            this.size=this.size()+ heap2.size();
//            this.treesCnt+=heap2.treesCnt;
//            this.markedNodesCnt+=heap2.markedNodesCnt;
//            this.treesCnt+=heap2.treesCnt;
            //number of trees,markedNodesCnt might change --> potential might change
        }
    }
    /**
     * public int size()
     *
     * Returns the number of elements in the heap.
     *
     */
    public int size()
    {
        return this.size;
    }

    /**
     * public int[] countersRep()
     *
     * Return an array of counters. The i-th entry contains the number of trees of order i in the heap.
     * Note: The size of of the array depends on the maximum order of a tree, and an empty heap returns an empty array.
     *
     */
    public int[] countersRep()
    {
        if (this.isEmpty()){
            return new int[0];
        }
        int[] counterArr = new int[(int)((Math.log(size()) / Math.log(2)) + 1)]; // we have at least log_2(n) trees
        HeapNode currTree=this.head;
        counterArr[currTree.getRank()]+=1;
        currTree=currTree.getNext();
        while(currTree!=this.head){
            counterArr[currTree.getRank()]+=1;
            currTree=currTree.getNext();
        }
        return counterArr;
    }

    /**
     * public void delete(HeapNode x)
     *
     * Deletes the node x from the heap.
     * It is assumed that x indeed belongs to the heap.
     *
     */
    public void delete(HeapNode x)
    {
        // decrease-key to infinity
        // TODO: 21/12/2021 can we insert a negative key?
        x.setKey(Integer.MIN_VALUE);
        this.decreaseKey(x, 0);
        this.deleteMin();
    }

    /**
     * public void decreaseKey(HeapNode x, int delta)
     *
     * Decreases the key of the node x by a non-negative value delta. The structure of the heap should be updated
     * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
     */
    public void decreaseKey(HeapNode x, int delta)
    {
        if (x.getParent() == null) { // x is a root
            x.setKey(x.getKey() - delta);
            if (x.getKey() < this.minNode.getKey()){ // no need to update min if it's not a root
                this.minNode = x;
            }
        } else if (x.getKey() - delta >= x.getParent().getKey()) { // no need to cut
            x.setKey(x.getKey() - delta);

        } else {
            x.setKey(x.getKey() - delta); // key is bigger than parent, need to cut
            cascadingCut(x, x.getParent());
        }
    }

    private void cascadingCut(HeapNode x, HeapNode parent){
        totalCuts++;
        cut(x, parent);
        if (parent.getParent() != null) { // parent is not a root
            if (!parent.isMarked()) { // this is the first cut = parent not marked
                parent.setMarked(true);
                markedNodesCnt++; // for calculating potential
            }
            else { // parent is marked
                cascadingCut(parent, parent.getParent());
            }
        }
    }

    private void cut(HeapNode child2Cut, HeapNode parent){
        child2Cut.setParent(null);
        if (child2Cut.isMarked()){
            this.markedNodesCnt--; // for potential counting
        }
        child2Cut.setMarked(false);
        parent.setRank(parent.getRank() - 1); // parent lost a child
        if (parent.getRank() == 0) { // parent lost his one and lonely child
            parent.setChild(null); // no more childs left for parent
            child2Cut.addAsNewTree(this.head, this.tail); // add child2Cut as a new tree
            this.head = child2Cut;
            if (child2Cut.getKey() < this.minNode.getKey()){ // no need to update min if it's not a root
                this.minNode = child2Cut;
            }
            this.treesCnt += 1; // for potential counting
        } else if (parent.getChild() == child2Cut) {
            parent.setChild(child2Cut.getNext());
            update(child2Cut);
        } else {
            update(child2Cut);
        }
    }

    private void update(HeapNode child2Cut) {
        child2Cut.getNext().setPrev(child2Cut.getPrev());
        child2Cut.getPrev().setNext(child2Cut.getNext());
        child2Cut.addAsNewTree(this.head, this.tail);
        this.head = child2Cut;
        if (child2Cut.getKey() < this.minNode.getKey()){ // no need to update min if it's not a root
            this.minNode = child2Cut;
        }
        this.treesCnt += 1; // for potential counting
    }


    /**
     * public int potential()
     *
     * This function returns the current potential of the heap, which is:
     * Potential = #trees + 2*#marked
     *
     * In words: The potential equals to the number of trees in the heap
     * plus twice the number of marked nodes in the heap.
     */
    public int potential()
    {
        return this.treesCnt+2*this.markedNodesCnt; // should be replaced by student code
    }

    /**
     * public static int totalLinks()
     *
     * This static function returns the total number of link operations made during the
     * run-time of the program. A link operation is the operation which gets as input two
     * trees of the same rank, and generates a tree of rank bigger by one, by hanging the
     * tree which has larger value in its root under the other tree.
     */
    public static int totalLinks()
    {
        return totalLinks;
    }

    /**
     * public static int totalCuts()
     *
     * This static function returns the total number of cut operations made during the
     * run-time of the program. A cut operation is the operation which disconnects a subtree
     * from its parent (during decreaseKey/delete methods).
     */
    public static int totalCuts()
    {
        return totalCuts; // should be replaced by student code
    }

    /**
     * public static int[] kMin(FibonacciHeap H, int k)
     *
     * This static function returns the k smallest elements in a Fibonacci heap that contains a single tree.
     * The function should run in O(k*deg(H)). (deg(H) is the degree of the only tree in H.)
     *
     * ###CRITICAL### : you are NOT allowed to change H.
     */
    public static int[] kMin(FibonacciHeap H, int k)
    {
        // implementation similar to class solution
        if (H.isEmpty() || k <= 0) {
            return new int[0];
        }
        int[] arr = new int[k];
        FibonacciHeap helpHeap = new FibonacciHeap();
        helpHeap.insert(H.findMin().getKey());
        for(int i = 0; i < k; i++) {
            arr[i] = helpHeap.minNode.getKey();
            HeapNode childInd = helpHeap.minNode.getChild();
            helpHeap.deleteMin();
//            each node has a lot of children, not just 2 unlike a binary heap
//            we'll insert the root and iterate over it's children
//            then find the min node in helpHeap again and repeat
            if (childInd != null) {
                HeapNode cur = childInd;
                helpHeap.insert(childInd.getKey());
                childInd = childInd.getNext();
                while (childInd!=cur){
                    helpHeap.insert(childInd.getKey());
                    childInd = childInd.getNext();
                }
            }
        }
        return arr;
    }
    public HeapNode getFirst() {
        return this.head;
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
    //todo-heapPrint ends here-delete afterwards.

    /**
     * public class HeapNode
     *
     * If you wish to implement classes other than FibonacciHeap
     * (for example HeapNode), do it in this file, not in another file.
     *
     */
    public static class HeapNode{

        public int key;
        //Todo- add info? which type? String? is it necessary?
        private int rank;
        private boolean marked;
        private HeapNode child;//leftmost child
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;

        public HeapNode(int key) {
            this.key = key;
        }

        public HeapNode(int key, HeapNode tail, HeapNode head) {
            this.key=key;
            this.prev=tail;
            this.next=head;
        }

        public int getKey() {
            return this.key;
        }
        public void setKey(int i) {
            this.key = i;
        }
        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }
        public boolean isMarked() {
            return marked;
        }

        public void setMarked(boolean marked) {
            this.marked = marked;
        }

        public HeapNode getChild() {
            return child;
        }

        public void setChild(HeapNode child) {
            this.child = child;
        }

        public HeapNode getNext() {
            return next;
        }

        public void setNext(HeapNode next) {
            this.next = next;
        }

        public HeapNode getPrev() {
            return prev;
        }

        public void setPrev(HeapNode prev) {
            this.prev = prev;
        }

        public HeapNode getParent() {
            return parent;
        }

        public void setParent(HeapNode parent) {
            this.parent = parent;
        }

        public void addAsNewTree(HeapNode head, HeapNode tail) {
            this.next = head;
            this.prev = tail;
            head.prev = this;
            tail.next = this;
        }
    }
}