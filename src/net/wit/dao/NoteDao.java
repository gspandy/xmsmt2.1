package net.wit.dao;

import java.util.List;

import net.wit.entity.DeliveryCenter;
import net.wit.entity.Note;

public interface NoteDao extends BaseDao<Note, Long> {

	List<Note> findListByDate(DeliveryCenter deliveryCenter, String date);
	
	Note findNote(Long noteId);
}
