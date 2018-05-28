import java.io.*;
import java.util.LinkedList;

public class Matching
{
	public static final int K = 6;
	public static HashTable strings;
	public static int remain;

	public static void main(String args[])
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		while (true)
		{
			try
			{
				String input = br.readLine();
				if (input.compareTo("QUIT") == 0)
					break;

				command(input);
			}
			catch (IOException e)
			{
				System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
			}
		}
	}

	private static void command(String input) throws IOException {
		String str = input.substring(2);

		if (input.charAt(0) == '<')
			scan(str);
		else if (input.charAt(0) == '@')
			print(str);
		else if (input.charAt(0) == '?')
			search(str);
		else
			throw new IOException();
	}

	private static void scan(String filename) {
		try {
			strings = new HashTable();
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String newString = in.readLine();
			int i = 0;

			while (newString != null) {
				for (int j = 0; j <= newString.length() - K; ++j) {
					String newSubstring = newString.substring(j, j + K);
					strings.insert(newSubstring, new Index(i, j));
				}

				newString = in.readLine();
				++i;
			}

		}
		catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void print(String index) {
		int i = Integer.parseInt(index.trim());
		System.out.println( strings.retrieve(i) );
	}

	private static void search(String pattern) {

		// divide the given pattern
		int num;
		int quotient = pattern.length() / K;
		remain = pattern.length() % K;

		if (remain > 0)
			num = quotient + 1;
		else
			num = quotient;

		String[] patterns = new String[num];

		for (int i = 0; i < quotient; i++)
			patterns[i] = pattern.substring(i * K, (i + 1) * K);

		if (remain > 0)
			patterns[quotient] = pattern.substring((quotient - 1) * K + remain, quotient * K + remain);

		// search for each pattern
		Index[][] results = searchSubPattern(patterns);

		// ensure if found substring concisely matches pattern
		printSearchResult(results, num);
	}

	private static Index[][] searchSubPattern(String[] patterns) {
		Index[][] results = new Index[patterns.length][];

		for (int i = 0; i < patterns.length; i++) {
			LinkedList<Index> found = strings.search(patterns[i]);

			if (found == null || found.isEmpty()) {
				results[i] = new Index[0];
				continue;
			}

			results[i] = new Index[found.size()];
			for (int j = 0; j < found.size(); j++) {
				results[i][j] = found.get(j);
			}
		}

		return results;
	}

	private static void printSearchResult(Index[][] results, int num) {
		String result = "";

		for (int i = 0; i < results[0].length; i++) {
			Index curr = results[0][i];
			boolean isMatch = true;

			for (int j = 1; j < num; j++) {
				Index[] rest = results[j];
				isMatch = false;
				int diff;

				if (remain != 0 && j == num - 1)
					diff = remain;
				else
					diff = K;

				for (int k = 0; k < rest.length; k++) {
					if (curr.compareTo(rest[k], diff)) {
						isMatch = true;
						curr = rest[k];
						break;
					}
				}
				if (!isMatch)
					break;
			}

			if (isMatch) {
				if (result.length() > 0)
					result += " ";
				result += results[0][i];
			}
		}

		if (result.length() == 0 || result == "")
			result += "(0, 0)";

		System.out.println(result);
	}

}

class Index {
	int x;
	int y;

	public Index(int x, int y) {
		this.x = x + 1;
		this.y = y + 1;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public boolean compareTo(Index other, int diff) {
		return (x == other.x) && (y + diff == other.y);
	}
}

class HashTable {
	private static final int MAX_TABLE = 100;
	private AVLTree[] substrings;

	public HashTable() {
		substrings = new AVLTree[MAX_TABLE];
	}

	public void insert(String newSubstring, Index index) throws Exception {
		// assume size < MAX_TABLE
		int i = hash(newSubstring);
		if (substrings[i] == null)
			substrings[i] = new AVLTree();

		substrings[i].add(newSubstring, index);
	}

	public String retrieve(int index) {
		if (substrings[index] == null)
			return "EMPTY";
		return substrings[index].preorder();
	}

	private int hash(String newSubstring) {
		// (k character들의 ASCII code들의 합) mod 100
		char[] ascii = newSubstring.toCharArray();
		int sum = 0;

		for (int i = 0; i < ascii.length; i++)
			sum += (int) ascii[i];

		return sum % 100;
	}

	public LinkedList<Index> search(String pattern) {
		int i = hash(pattern);
		if (substrings[i] == null)
			return null;
		return substrings[i].search(pattern);
	}
}

class AVLTree {
	private AVLTreeNode root;

	public AVLTree() {
		root = null;
	}
	public boolean isEmpty() {
		return (root == null);
	}
	public void add(String substring, Index index) {
		if (isEmpty())
			root = new AVLTreeNode(substring, index);
		else
			root = root.add(substring, index);
	}
	public String preorder() {
		if (isEmpty()) return "EMPTY";
		else {
			String result = preorder(root);
			return result.substring(0, result.length() - 1);
		}
	}
	private String preorder(AVLTreeNode root) {
		String result = "";
		if (root != null) {
			result += (root.getSubstring() + " ");
			result += preorder(root.getLeft());
			result += preorder(root.getRight());
		}
		return result;
	}

	public LinkedList<Index> search(String substring) {
		if (isEmpty())
			return null;

		return root.search(substring);
	}
}

class AVLTreeNode {
	private String substring;
	private LinkedList<Index> list;
	private AVLTreeNode leftChild;
	private AVLTreeNode rightChild;

	public AVLTreeNode(String substring, Index index) {
		this.substring = substring;
		list = new LinkedList<>();
		list.add(index);
		leftChild = null;
		rightChild = null;
	}

	public String getSubstring() {
		return substring;
	}

	public LinkedList<Index> getList() {
		return list;
	}

	public AVLTreeNode getLeft() {
		return leftChild;
	}

	public void setLeft(AVLTreeNode leftChild) {
		this.leftChild = leftChild;
	}

	public AVLTreeNode getRight() {
		return rightChild;
	}

	public void setRight(AVLTreeNode rightChild) {
		this.rightChild = rightChild;
	}

	private int leftHeight() {
		return (leftChild == null) ? 0 : leftChild.height();
	}

	private int rightHeight() {
		return (rightChild == null) ? 0 : rightChild.height();
	}

	public int height() {
		return (leftHeight() > rightHeight()) ? (leftHeight() + 1) : (rightHeight() + 1);
	}

	public AVLTreeNode add(String substring, Index index) {
		// insert after a leaf (or into an empty tree)
		int cmp = substring.compareTo( this.substring );

		// if same substring found, just add the index
		if (cmp == 0)
			list.add(index);
		// branch left로 내려간다
		else if (cmp < 0 )
			leftChild = (leftChild == null) ? new AVLTreeNode(substring, index) : leftChild.add(substring, index);
		// branch right로 내려간다
		else if (cmp > 0)
			rightChild = (rightChild == null) ? new AVLTreeNode(substring, index) : rightChild.add(substring, index);
		return this.balance();
	}

	public AVLTreeNode balance() {
		int diff = leftHeight() - rightHeight();
		// at most 1 difference is accepted
		if (diff < 2 && diff > -2)
			return this;
		// left is higher
		if (diff > 0) {
			// check for double rotation
			if (leftChild.leftHeight() < leftChild.rightHeight())
				leftChild = leftChild.rotateLeft();
			// rotate towards right
			return this.rotateRight();
		}
		// right is higher
		else {
			// check for double rotation
			if (rightChild.leftHeight() > rightChild.rightHeight())
				rightChild = rightChild.rotateRight();
			// rotate towards left
			return this.rotateLeft();
		}
	}

	private AVLTreeNode rotateLeft() {
		// set right child as root
		AVLTreeNode newRoot = this.rightChild;
		this.rightChild = newRoot.leftChild;
		newRoot.leftChild = this;

		return newRoot;
	}

	private AVLTreeNode rotateRight() {
		// set left child as root
		AVLTreeNode newRoot = this.leftChild;
		this.leftChild = newRoot.rightChild;
		newRoot.rightChild = this;

		return newRoot;
	}

	public LinkedList<Index> search(String substring) {
		int cmp = substring.compareTo( this.substring );

		if (cmp < 0) {
			if (leftChild == null)
				return null;
			return leftChild.search(substring);
		}
		else if (cmp > 0) {
			if (rightChild == null)
				return null;
			return rightChild.search(substring);
		}
		else
			return list;
	}
}