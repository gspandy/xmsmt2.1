package net.wit.service;

import java.util.List;

import net.wit.entity.DeliveryCenter;
import net.wit.entity.Note;

public interface NoteService extends BaseService<Note, Long> {

	List<Note> findListByDate(DeliveryCenter deliveryCenter, String date);
	
	Note findNote(Long noteId);
}
