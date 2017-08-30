package loop;

import java.util.HashSet;

public class LinkQueue {
	private static  HashSet<Object> visited = new HashSet<>();
	private static Queue<Object> unVisited = new Queue<>();
	
	public static Queue<Object> getUnVisited() {
		return unVisited;
	}
	/**
	 * ���ѷ��ʼ��������Ԫ��
	 * @param obj
	 */
	public static void addVisited(Object obj) {
		visited.add(obj);
	}
	/**
	 * ���ѷ��ʼ���visited���Ƴ��ѷ���obj
	 * @param obj
	 */
	public static void removeVisited(Object obj) {
		visited.remove(obj);
	}
	/**
	 * ��֤ÿ��objֻ������һ��
	 * @param obj
	 */
	public static void addUnVisited(Object obj) {
		if (obj != null && !visited.contains(obj) && !unVisited.contains(obj)) {
			unVisited.offer(obj);
		}
	}
	/**
	 * ����
	 * @return
	 */
	public static Object removeUnVisited() {
		return unVisited.remove();
	}
	/**
	 * �����ѷ�����ַ������
	 * @return visited���ϳ���
	 */
	public static int getVisitedSize() {
		return visited.size();
	}
	/**
	 * �ж�δ���ʼ����Ƿ�Ϊ��
	 * @return Ϊ�շ���true
	 */
	public static boolean isUnVisitedEmpty() {
		return unVisited.isEmpty();
	}
}
