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
            nodeToInsert = new HeapNode(key, this.head, this.tail);
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
    public void deleteMin()
    {
        HeapNode minHeapNode=this.minNode;




        if(this.head==this.tail) {
            if (this.size == 1){
                // this.min is the root of a single tree in the heap
                this.minNode = null;
                this.head = null;
                this.tail = null;
                this.treesCnt = 0;
                this.size = 0;
            }else {
                // changes the children of min to be roots of a tree-
                // by that deleting the original root
                updateRoots(this.minNode);
                //maintaing the order of children placement in the heap and by so removing this.min
                this.minNode.getChild().getPrev().setNext(this.minNode.getNext()); // x_k -> y_3
                this.minNode.getNext().setPrev(this.minNode.getChild().getPrev()); // y_3 -> x_k
                this.minNode.getChild().setPrev(minNode.getPrev()); // x_1 -> y_1
                minNode.getPrev().setNext(this.minNode.getChild()); // y_1 -> x_1
                this.treesCnt-=1;
            }
        }
        else {
            //temporarly change the minNode pointer to another roots existing in the heap- might change after consolidation
            this.minNode=minHeapNode.getNext();
            // successive linking- creates a valid Binomial Heap from FibHeap
            consolidation();
        }
        // deletion of one heapNode is done so the size need an update
        this.size=this.size-1;



        //minNode,rootsNum maintainace is done in consolidation.
    }

    private void consolidation() {
        // successive linking- creates a valid Binomial Heap from FibHeap

        HeapNode [] rankArray = new HeapNode[this.treesCnt];
        HeapNode currRoot = this.head.getNext();
        int rankindex = 0;

        while (currRoot.getNext()!=null){
            int i=0;
            rankindex=currRoot.getRank();
            i=rankindex;
            // empty box
            if (rankArray[rankindex]==null){ //there is no root with the same rank
                rankArray[rankindex]=currRoot;
            }
            else { // there is a tree with the same rank as currRoot
                // we should link them
                HeapNode newTree;
                if (currRoot.getKey()<rankArray[i].getKey()){
                    //currRoot should be the root
                    newTree= link(currRoot,rankArray[i]);
                }
                else{
                    //rankArray[i] should be the root
                    newTree= link(rankArray[i],currRoot);
                }
                i++;
                while (rankArray[i]!=null){
                    if (currRoot.getKey()<rankArray[i].getKey()){
                        //currRoot should be the root
                        newTree= link(currRoot,rankArray[i]);
                    }
                    else{
                        //rankArray[i] should be the root
                        newTree= link(rankArray[i],currRoot);
                    }
                    i++;
                }
                // rankArray[i]==null
                rankArray[i]=newTree;
            }
        }
        // now we should make sure the roots "list" is ordered by increasing rank
        //so we will collect trees with higher rank first and assign it as the last
        this.minNode =null;
        this.treesCnt=0;
        HeapNode currNext=null; //todo- check logic
        for(int j=rankArray.length-1;j>=0;j--){
            if(rankArray[j]!=null){
                if (this.minNode==null){
                    //the heap is empty
                    this.tail=rankArray[j];
                    this.head =rankArray[j];
                    this.minNode=rankArray[j];
                    this.treesCnt=1;
                    currNext=this.tail;
                }
                else{
                    //roots list isnt empty-we should update pointers
                    // "inserting" currTree into the list of roots.
                    //todo-take care of pointers last.next first.prev
                    //for each nonempty index theres a tree with its rank so rootsNum
                    // so rootsNum should get increased by 1
                    this.treesCnt+=1;
                    rankArray[j].setNext(currNext);
                    currNext.setPrev(rankArray[j]);
                    this.head=rankArray[j]; // in each iteration new head is inserted
                    currNext=rankArray[j];

                    //maintain the minNode
                    if(rankArray[j].key < this.minNode.key){
                        this.minNode=rankArray[j];
                    }
                }
            }

        }
        //maintain pointers of circular list
        this.head.setPrev(this.tail);
        this.tail.setNext(this.head);

    }

    private HeapNode link(HeapNode smaller, HeapNode bigger) {
        //todo- check link counter
        //link bigger to smaller
        //remove heapNode from rootsList
        removeHeapNode(bigger);
        addHeapNodeAsFirst(smaller.getChild(),bigger);
        smaller.setRank(smaller.getRank()+1);
        bigger.setMarked(false);
        totalLinks++;
        return smaller;
    }

    private void addHeapNodeAsFirst(HeapNode child, HeapNode bigger) {
//        //todo-implement addHeapNodeAsFirst if necessary
//        if (child==null){
//            // no children for smaller
//
//        }

    }

    private void removeHeapNode(HeapNode bigger) {
        //this method removes a given heap node from the list of roots
        // @pre: bigger is in the heap
        if (bigger==this.head){
            //bigger is the head of rootsList
            this.head=this.head.getNext();
            this.tail.setNext(this.head);
            this.head.setPrev(this.tail);
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
            this.head.setPrev(this.tail);
            this.tail.setNext(this.head);
            //number of trees doesnt change-potential doesnt change

        }
        if(!this.isEmpty()&& !heap2.isEmpty()){
            //if !this.isEmpty()&& heap2.isEmpty() - nothing to do
            // next case we have to deal with- both not empty
            this.tail.setNext(heap2.head);
            heap2.head.setPrev(this.tail);
            this.tail=heap2.tail;
            this.tail.setNext(this.head);
            this.head.setPrev(this.tail);
            //maintain minHeapNode
            if((this.minNode==null) ||(heap2.minNode!=null)&& heap2.minNode.getKey()<this.minNode.getKey()){
                this.minNode=heap2.minNode;
            }
            //maintain other fields
            this.size=this.size()+ heap2.size();
            this.treesCnt+=heap2.treesCnt;
            this.markedNodesCnt+=heap2.markedNodesCnt;
            this.treesCnt+=heap2.treesCnt;
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
        int[] arr = new int[100];
        return arr; //	 to be replaced by student code
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
        if (parent.getParent() != null) { // parent not is a root
            if (!parent.isMarked()) { // this is the first cut = parent not marked
                parent.setMarked(true);
                markedNodesCnt++; // for calculating potential
            }
            else { // parent is marked
                cascadingCut(parent, parent.getParent());
            }
        }
    }

    private void cut(HeapNode x, HeapNode parent){
        x.setParent(null);
        if (x.isMarked()){
            this.markedNodesCnt--; // for potential counting
        }
        x.setMarked(false);
        parent.setRank(parent.getRank()-1); // parent lost a child
        if (parent.getRank() == 0) { // parent lost his one and lonely child
            parent.setChild(null); // no more childs left for parent
            x.changePointers(this.head, this.tail); // asuming x is the child of parent
            this.head = x;
            if (x.getKey() < this.minNode.getKey()){ // no need to update min if it's not a root
                this.minNode = x;
            }
            this.treesCnt += 1; // for potential counting
        } else if (parent.getChild() == x) {
            parent.setChild(x.getNext());
            update(x);
        } else {
            update(x);
        }
    }

    private void update(HeapNode x) {
        x.getNext().setPrev(x.getPrev());
        x.getPrev().setNext(x.getNext());
        x.changePointers(this.head, this.tail);
        this.head = x;
        if (x.getKey() < this.minNode.getKey()){ // no need to update min if it's not a root
            this.minNode = x;
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
        return treesCnt+2*markedNodesCnt; // should be replaced by student code
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
    }

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

        public HeapNode(int key, HeapNode head, HeapNode tail) {
            this.key=key;
            this.prev=tail;
            this.next=head;
            this.parent=null;
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
        public void changePointers(HeapNode x, HeapNode y) {
            this.next = x;
            this.prev = y;
            x.prev = this;
            y.next = this;
        }
    }
}