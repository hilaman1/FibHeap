
import java.util.Arrays;


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
        this.size=0;
        this.minNode =null;
        this.head =null;
        this.tail =null;
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
            nodeToInsert.setNext(nodeToInsert);
            nodeToInsert.setPrev(nodeToInsert);
            this.head = nodeToInsert;
            this.tail = nodeToInsert;
            this.minNode = nodeToInsert;
        }else{
            nodeToInsert = new HeapNode(key, this.tail, this.head);
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
        if (!this.isEmpty()){
            if (this.minNode!= null) {
                removeMin(this.minNode); //size is updated in remove min
                if (this.size()>0){ // if after removeHeap the heap still has nodes then consolidation is needed.
                    consolidation();
                }
            }
        }
    }

    private void removeMin(HeapNode minNode) {
        if (minNode!=null) {
            HeapNode pointerToPrevOfMin=this.minNode.getPrev();
            HeapNode pointerToNextOfMin=this.minNode.getNext();
            HeapNode pointerToChildMinNode = this.minNode.getChild();
            //at least one heapNode
            if (this.treesCnt == 1) {
                //only minNode in heap
                if (this.size()==1){
                    initializeFields();
                }
                else {
                    this.head=this.minNode.getChild();
                    this.tail=this.minNode.getChild().getPrev();
                    updateRoots();
                    this.size--;
                }

            } else { //size>1
                if (minNode.getRank() == 0) {
                    //there is more than 0 node in the heap minNode doesn't have children
                    //no need to take care of pointers of its children
                    //removeHeapNode(this.minNode); // treeCnt is updated inside the method
                    if (this.minNode==this.head){
                        //bigger is the head of rootsList
                        this.head=this.head.getNext();
                    }
                    if (this.minNode==this.tail){
                        this.tail=this.minNode.getPrev();
                    }
                    this.minNode.getPrev().setNext(this.minNode.getNext());
                    this.minNode.getNext().setPrev(this.minNode.getPrev());
                    this.treesCnt--;
                } else {
                    //minNode has at least one Child
                    if (this.minNode == this.head) { //minNode is the head of the root List
                        HeapNode lastChild = this.minNode.getChild().getPrev(); //todo- what happen when only one child?
                        //circular list so last child is th prev to child
                        this.head = this.minNode.getChild();
                        lastChild.setNext(this.minNode.getNext());
                        this.minNode.getNext().setPrev(lastChild);
                        this.head.setPrev(this.tail);
                        this.tail.setNext(this.head);
                        updateRoots();
                    } else if (this.minNode == this.tail) { //minNode is the tail of the root List
                        HeapNode prevHN = this.minNode.getPrev();
                        HeapNode lastChild = this.minNode.getChild().getPrev(); //circular list so last child is th prev to child
                        prevHN.setNext(this.minNode.getChild());
                        this.minNode.getChild().setPrev(prevHN);
                        this.head.setPrev(lastChild);
                        this.tail=lastChild;
                        updateRoots();

                    } else { // this min node is in between && has at least one child
                        pointerToChildMinNode.getPrev().setNext(pointerToNextOfMin); // x_k -> y_3
                        pointerToNextOfMin.setPrev(pointerToChildMinNode.getPrev()); // y_3 -> x_k
                        pointerToChildMinNode.setPrev(pointerToPrevOfMin); // x_1 -> y_1
                        pointerToPrevOfMin.setNext(pointerToChildMinNode); // y_1 -> x_1
                        updateRoots();
                    }
                }
                this.size--;
            }

        }
    }

    private void consolidation() {
        // successive linking- creates a valid Binomial Heap from FibHeap

        HeapNode [] rankArray = new HeapNode[(int)(Math.log(this.size) / Math.log(2)) + 1]; // there are log_2(n) trees at most
        HeapNode currRoot = this.head;
        currRoot.setParent(null);
        currRoot.setMarked(false);
        HeapNode nextRoot= currRoot.getNext();
        int cntIteration=0;
        int currRank=0;
        if (rankArray[currRoot.getRank()]==null){
            rankArray[currRoot.getRank()]=currRoot;
        }
        currRoot=currRoot.getNext();

        nextRoot=currRoot.getNext();
        cntIteration++;

        int treeCounterAtFirst=this.treesCnt;
        while (currRoot!=null && currRoot!=this.head && cntIteration < treeCounterAtFirst){
            currRoot.setParent(null);
            currRoot.setMarked(false);
            HeapNode xRoot=currRoot;
            currRank=xRoot.getRank();
            nextRoot=currRoot.getNext();

            // empty box
            if (rankArray[currRank]==null){ //there is no root with the same rank
                rankArray[currRank]=xRoot;
            }
            else { // there is a tree with the same rank as currRoot
                // we should link them
                while (currRank<rankArray.length && rankArray[currRank]!=null){
                    HeapNode sameRankTree=rankArray[currRank];
                    xRoot=link(xRoot,sameRankTree);
                    rankArray[currRank]=null; //after linking two trees with rank r ==> currently  no tree in rank r.
                    currRank++;
                }
                rankArray[xRoot.getRank()]=xRoot; // linking is done
            }
            currRoot=nextRoot;
            cntIteration++;
        }

        // now we should make sure the roots "list" is ordered by increasing rank
        //so we will collect trees with lower rank first and insert after them

        this.minNode=null;
        this.treesCnt=0;
        HeapNode pos=null; //pointer to currTree
        for(int j=0;j<rankArray.length;j++){
            if(rankArray[j]!=null){
                if (this.minNode==null){
                    //the heap is empty
                    pos=rankArray[j];
                    pos.setNext(pos);
                    pos.setPrev(pos);
                    this.tail=pos;
                    this.head =pos;
                    this.minNode=pos;
                }
                else{
                    //roots list isnt empty-we should update pointers
                    // "inserting" currTree into the list of roots.
                    //for each nonempty index theres a tree with its rank so rootsNum
                    // so treesCnt should get increased by 1
                    insertAfter(pos,rankArray[j]);
                    //rankArray[j] becomes a root so is mark=false;
                    rankArray[j].setMarked(false);
                    this.tail=rankArray[j];//tail is updated though out the loop
                    //this.size+=rankArray[j].getRank()+1;
                    //maintain the minNode
                    if(rankArray[j].getKey() < this.minNode.getKey()&& rankArray[j].getKey()< pos.getKey()){
                        this.minNode=rankArray[j];
                        pos=rankArray[j];
                    }
                    else {
                        pos=pos.getNext();
                    }
                }
                this.treesCnt+=1;//inserted new tree to heap
            }
        }
        //maintain pointers of circular list
        this.head.setPrev(this.tail);
        this.tail.setNext(this.head);

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
            removeHeapNode(rootToLink);// treeCnt is updated inside
            insertHeapNodeAsFirst(currRoot,rootToLink);
            //link done
            totalLinks++;
            return currRoot;

        }else { // currRoot is bigger
            removeHeapNode(currRoot); // treeCnt is updated inside
            insertHeapNodeAsFirst(rootToLink,currRoot);
            //link done
            totalLinks++;
            return rootToLink;
        }
    }


    private void insertHeapNodeAsFirst(HeapNode smaller, HeapNode bigger) {

        if (smaller.getRank()==0){
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

        }
        smaller.setRank(smaller.getRank()+1);
        bigger.setMarked(false);
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
            if (prevHN.getNext()!=null){
                prevHN.getNext().setPrev(prevHN);
            }
            if (bigger==this.tail){
                //bigger is the last tree in rootsList
                this.tail=prevHN;
            }
        }
        this.treesCnt-=1;

    }

    private void updateRoots(){
        if (this.minNode!=null){
            if (this.minNode.getRank() > 0){
                HeapNode currChild=this.minNode.getChild();
                currChild.setParent(null);
                this.treesCnt++;
                if(currChild.isMarked()){
                    currChild.setMarked(false);
                    this.markedNodesCnt--;
                }
                for (int i=2; i<=this.minNode.getRank();i++){
                    currChild.setParent(null);
                    this.treesCnt++;
                    if(currChild.isMarked()){
                        currChild.setMarked(false);
                        this.markedNodesCnt--;
                    }
                }
                this.treesCnt--;

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
        if (this.isEmpty()){
            return new int[]{};
        }
        int[] counterArr = new int[(int)(Math.log(this.size) / Math.log(2)) + 1];
        HeapNode currTree=this.head;
        int cnt=0;
        while(cnt<this.treesCnt){
            counterArr[currTree.getRank()]+=1;
            currTree=currTree.getNext();
            cnt++;
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
            if (x==this.head){
                this.head=x;
            }
            else if (x==this.tail){
                this.tail=x;
            }
            if (x.getKey() < this.minNode.getKey()){ // no need to update min if it's not a root
                this.minNode = x;
            }
        }
        else if (x.getKey() - delta >= x.getParent().getKey()) { // no need to cut
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
        return treesCnt+2*markedNodesCnt;
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
        helpHeap.minNode.ORIGINALpointer=H.minNode;
        for(int i = 0; i < k; i++) {
            arr[i] = helpHeap.minNode.getKey();
            HeapNode childInd = helpHeap.minNode.ORIGINALpointer.getChild();
            helpHeap.deleteMin();
//            each node has a lot of children, not just 2 unlike a binary heap
//            we'll insert the root and iterate over it's children
//            then find the min node in helpHeap again and repeat
            if (childInd != null) {
                HeapNode cur = childInd;
                helpHeap.insert(childInd.getKey()).ORIGINALpointer=childInd;

                childInd = childInd.getNext();
                while (childInd != cur){
                    helpHeap.insert(childInd.getKey()).ORIGINALpointer=childInd;

                    childInd = childInd.getNext();
                }
                helpHeap.insert(childInd.getKey()).ORIGINALpointer=childInd;
            }
        }
        return arr;
    }


    public HeapNode getFirst() {
        return this.head;
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
        private int rank;
        private boolean marked;
        private HeapNode child;//leftmost child
        private HeapNode next;
        private HeapNode prev;
        private HeapNode parent;
        public HeapNode ORIGINALpointer;

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
        public void changePointers(HeapNode x, HeapNode y) {
            this.next = x;
            this.prev = y;
            x.prev = this;
            y.next = this;
        }
    }
}