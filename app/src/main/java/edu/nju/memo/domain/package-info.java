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
 * <p>
 * Create a MemoItem manually:
 * <ol>
 * <li>User intends to create an item by himself.</li>
 * <li>Create an instance via constructor. It's totally empty now. Show it on screen.</li>
 * <li>User customizes it and confirms.</li>
 * <li>Set MemoItem's fields as well as create attachments if necessary. Call MemoDao.insert.</li>
 * </ol>
 * </p>
 * <p>
 * Refer to MemoItem and Attachment for details.
 */
package edu.nju.memo.domain;
