package net.wit.dao.impl;

import java.util.List;
import javax.persistence.Query;

import net.wit.dao.NoteDao;
import net.wit.entity.DeliveryCenter;
import net.wit.entity.Note;

import org.springframework.stereotype.Repository;

@Repository("noteDaoImpl")
public class NoteDaoImpl extends BaseDaoImpl<Note, Long> implements NoteDao {

	@SuppressWarnings("unchecked")
	public List<Note> findListByDate(DeliveryCenter deliveryCenter, String date) {
		if(deliveryCenter==null){
			return null;
		}
		String jpql = "select * from xx_note where create_Date > '"+date+"' and delivery_Center = "+deliveryCenter.getId();
		Query query = entityManager.createNativeQuery(jpql, Note.class);
		List<Note> list = query.getResultList();
		return list;
	}

	public Note findNote(Long noteId) {
		String jpql = "select * from xx_note where id = "+noteId;
		Query query = entityManager.createNativeQuery(jpql, Note.class);
		Note note = (Note) query.getResultList().get(0);
		return note;
	}
}
