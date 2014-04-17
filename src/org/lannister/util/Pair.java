package org.lannister.util;
/**
author = 'Oguz Demir'
 */
public class Pair<A extends Comparable<A>, B extends Comparable<B>> implements Comparable<Pair<A, B>> {

	A a;
	
	B b;
	
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A first() { return a; }
	
	public B second() { return b; }

	@Override
	public int compareTo(Pair<A, B> p) {
		return a.compareTo(p.a) != 0 ? a.compareTo(p.a) : b.compareTo(b);
	}
}
