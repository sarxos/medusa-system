package com.sarxos.medusa.trader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.sarxos.medusa.data.DBDAO;
import com.sarxos.medusa.market.Paper;
import com.sarxos.medusa.market.Symbol;


/**
 * Papers wallet.
 * 
 * TODO: need to reimplement to interact strictly with traders
 * 
 * @author Bartosz Firyn (SarXos)
 */
public class Wallet {

	/**
	 * List of papers I have or would like to have.
	 */
	private List<Paper> papers = new LinkedList<Paper>();

	/**
	 * Wallet instance.
	 */
	private static Wallet instance = new Wallet();

	/**
	 * Database DAO.
	 */
	private DBDAO dbdao = DBDAO.getInstance();

	/**
	 * Private constructor.
	 */
	private Wallet() {
		this.reload();
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
	 * @return true if papers has been added, false otherwise
	 */
	public boolean addPaper(Paper paper) {
		boolean found = false;
		ListIterator<Paper> pi = papers.listIterator();
		while (pi.hasNext()) {
			if (pi.next().getSymbol() == paper.getSymbol()) {
				found = true;
				break;
			}
		}
		boolean added = false;
		if (!found) {
			added = papers.add(paper.clone());
		}
		return added;
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
	public boolean removePaper(Paper paper) {
		boolean removed = false;
		ListIterator<Paper> pi = papers.listIterator();
		while (pi.hasNext()) {
			if (pi.next().getSymbol() == paper.getSymbol()) {
				pi.remove();
				removed = true;
				break;
			}
		}
		return removed;
	}

	/**
	 * Update paper.
	 * 
	 * @param paper
	 * @return true if paper has been updated, false otherwise
	 */
	public boolean updatePaper(Paper paper) {
		boolean updated = false;
		ListIterator<Paper> pi = papers.listIterator();
		Paper p = null;
		while (pi.hasNext()) {
			p = pi.next();
			if (p.getSymbol() == paper.getSymbol()) {
				p.setQuantity(paper.getQuantity());
				p.setDesiredQuantity(paper.getDesiredQuantity());
				updated = true;
				break;
			}
		}
		return updated;
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

	/**
	 * Force reload wallet from the database.
	 */
	public void reload() {
		papers = dbdao.getPapers();
	}
}
