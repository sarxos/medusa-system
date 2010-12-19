package com.sarxos.gpwnotifier.trader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.sarxos.gpwnotifier.market.Paper;
import com.sarxos.gpwnotifier.market.Symbol;


/**
 * Papers wallet.
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Wallet {

	/**
	 * List of papers I have.
	 */
	private List<Paper> papers = new LinkedList<Paper>();

	/**
	 * Wallet instance.
	 */
	private static Wallet instance = new Wallet();
	
	
	/**
	 * Private constructor.
	 */
	private Wallet() {
	}
	
	/**
	 * @return Return {@link Wallet} singleton instance.
	 */
	public static Wallet getInstance() {
		return instance;
	}
	
	/**
	 * Add paper to the wallet.
	 * 
	 * @param paper - paper to add
	 * @return Total quantity of given paper
	 */
	public int add(Paper paper) {
		
		ListIterator<Paper> pi = papers.listIterator();

		int n, k;
		int q = paper.getQuantity();
		
		boolean exist = false;
		
		Paper p = null;
		while (pi.hasNext()) {
			p = pi.next();
			if (p.getSymbol() == paper.getSymbol()) {
				exist = true;
				n = p.getQuantity();
				k = paper.getQuantity();
				p.setQuantity(q = n + k);
				break;
			}
		}
		if (!exist) {
			papers.add(paper.clone());
		}
		
		return q;
	}
	
	/**
	 * @return Return all papers.
	 */
	public List<Paper> getPapers() {
		List<Paper> papers = new ArrayList<Paper>(this.papers.size());
		papers.addAll(this.papers);
		return papers;
	}
	
	/**
	 * Remove given paper.
	 * 
	 * @param paper - paper to remove
	 * @return Total quantity of given paper.
	 */
	public int remove(Paper paper) {
		
		ListIterator<Paper> pi = papers.listIterator();
		
		int n = paper.getQuantity();
		int q = 0;
		int k = 0;
		
		Paper p = null;
		while (pi.hasNext()) {
			p = pi.next();
			if (p.getSymbol() == paper.getSymbol()) {
				q = p.getQuantity();
				if (q < n) {
					throw new IllegalArgumentException(
							"Cannot remove so many papers (" + n + "). " +
							"There are only " + p.getQuantity() + " " + p.getSymbol() + " " +
							"papers in the wallet."
					);
				} else if (q == n) {
					pi.remove();
					k = 0;
					break;
				} else if (q > n) {
					p.setQuantity(k = q - n);
					break;
				}
			}
		}

		return k;
	}
	
	/**
	 * @param symbol - symbol to find
	 * @return Return paper with given symbol or null if paper doses not exist
	 */
	public Paper getPaper(Symbol symbol) {

		if (symbol == null) {
			throw new IllegalArgumentException("Paper symbol cannot be null");
		}
		
		ListIterator<Paper> pi = papers.listIterator();
		Paper p = null;
		
		while (pi.hasNext()) {
			p = pi.next();
			if (p.getSymbol() == symbol) {
				return p;
			}
		}
		
		return null;
	}
	
	/**
	 * @param paper - paper to find (on the symbol base)
	 * @return Return paper with given symbol or null if paper doses not exist
	 * @see Wallet#getPaper(Symbol)
	 */
	public Paper getPaper(Paper paper) {
		
		if (paper == null) {
			throw new IllegalArgumentException("Paper cannot be null");
		}
		
		return getPaper(paper.getSymbol());
	}
}
