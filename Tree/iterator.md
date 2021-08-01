####  二叉树的创建
```java
/**
 * 创建一棵二叉树
 * @author xpwang5
 *
 */
	public class BinaryTree {
	  TreeNode root; // 根节点
	
	public BinaryTree(TreeNode root) {
		this.root = root;
	}
	
	public BinaryTree() {
	}
	
//	
    
	/**
	 * ①先判断是否有根节点,没有的话先创建根节点
	 * ②判断数据的值，与当前节点比较，较小放在左子树，较大则放在右子树；
	 * ③如果当前左子树是空的，则当前节点就是左子树，否则继续往下递归
	 * 
	 * @param node
	 * @param data
	 */
	public void createBinaryTree(TreeNode node,Integer data) {
		if(root==null) {
			root=new TreeNode(data); // 初始化的时候如果没有根节点。
		}else if(data<node.data) {   
			if(node.left==null) {
				node.left=new TreeNode(data);
			}else {
				createBinaryTree(node.left,data);
			}
		}else {
			if(node.right==null) {
				node.right=new TreeNode(data);
			}else {

				createBinaryTree(node.right,data);
			}
		}
	}
```
#### 二叉树的前序遍历
``` java
/**
	 * 树的前序遍历
	 * 
	 */
	public void preorder(TreeNode node) {
		if(node==null) {
			return;
		}
			System.out.println(node.data);
			preorder(node.left);
			preorder(node.right);
	}
	
	/**
	 * 树的迭代方式前序遍历
	 * ①先创建一个栈结构
	 * ②将根节点当做临时的treeNode入栈，当左子节点不为空的时候，将左子节点入栈，
	 * ③当左子节点为空的时候弹出来，判断右子树是否存在。
	 */
	public void preiterationorder(TreeNode node) {
		Stack<TreeNode> stack = new Stack<>();
		TreeNode treeNode = root;
		while(!stack.isEmpty()||treeNode!=null) {
		
			while(treeNode!=null) {
				System.out.println(treeNode.data);
				stack.push(treeNode);
				treeNode=treeNode.left;
			}
			
			if(!stack.isEmpty()) {
				treeNode=stack.pop();
				treeNode=treeNode.right;
			}					
		}	
	 }
	
```
#### 二叉树的层次遍历
```java
/**
	 * 树的层次遍历
	 * ①创建一个队列，将根节点放入队列
	 * ②将队列中的元素出队列，并将它的左右子树放入队列
	 * ③直到队列的元素为空，结束遍历。
	 */
	public void levelorder(TreeNode root) {
		Queue<TreeNode> queue = new LinkedList<TreeNode>() ;
		queue.offer(root);
		while(!queue.isEmpty()) {
			TreeNode node=queue.poll();
			System.out.println(node.data);
			if(node.left!=null) {
				queue.offer(node.left);
			}
			if(node.right!=null) {
				queue.offer(node.right);
			}
		}
		
	}
```

---------------------------------
#### leecode相关的练习
- [102. 二叉树的层序遍历](https://leetcode-cn.com/problems/binary-tree-level-order-traversal/) [优质题解]（https://leetcode-cn.com/problems/binary-tree-level-order-traversal/solution/bfs-de-shi-yong-chang-jing-zong-jie-ceng-xu-bian-l/）
