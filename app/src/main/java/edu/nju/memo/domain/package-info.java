/**
 * This package contains entities Memo and Attachment,
 * plus description of their persistence Entities.kt
 * <p>
 * Create a Memo from outer:
 * <ol>
 * <li>Our app is launched via share or user intending to save the clipboard.</li>
 * <li>Then call the proper MemoItemFactory.getMemoItem to get an instance of Memo.</li>
 * <li>Now user can get chance to inspect and modify this item, depending the way of interacting.
 * Show this one on screen</li>
 * <li>After modification or skipped, user confirms. Call MemoDao.insert to persist it.</li>
 * </ol>
 * </p>
 * <p>
 * Create a Memo manually:
 * <ol>
 * <li>User intends to create an item by himself.</li>
 * <li>Create an instance via constructor. It's totally empty now. Show it on screen.</li>
 * <li>User customizes it and confirms.</li>
 * <li>Set Memo's fields as well as create attachments if necessary. Call MemoDao.insert.</li>
 * </ol>
 * </p>
 * <p>
 * Refer to Memo and Attachment for details.
 */
package edu.nju.memo.domain;
