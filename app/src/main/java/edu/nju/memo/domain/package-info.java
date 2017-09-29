/**
 * This package contains entities MemoItem and Attachment,
 * plus description of their persistence Entities.kt
 * <p>
 * Create a MemoItem from outer:
 * <ol>
 * <li>Our app is launched via share or user intending to save the clipboard.</li>
 * <li>Then call the proper MemoItemFactory.getMemoItem to get an instance of MemoItem.</li>
 * <li>Now user can get chance to inspect and modify this item, depending the way of interacting.
 * Show this one on screen</li>
 * <li>After modification or skipped, user confirms. Call MemoDao.insert to persist it.</li>
 * </ol>
 * </p>
 * Notice:
 * <ul>
 * <li>2.1 This instance is created according to info extracted from the parameter,
 * so it may not complete yet. See MemoItem's & Attachment's doc for detail.</li>
 * <li>2.2 If this item is concerned about images, they will be cached to a temp file during this step.</li>
 * <li>3.1 If this item is conc</li>
 * </ul>
 */
package edu.nju.memo.domain;
