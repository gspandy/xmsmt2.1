package net.wit.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;

import net.wit.dao.NoteReplyDao;
import net.wit.entity.Note;
import net.wit.entity.NoteReply;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository("noteReplyDaoImpl")
public class NoteReplyDaoImpl extends BaseDaoImpl<NoteReply, Long> implements NoteReplyDao {
	
	@PersistenceContext
	protected EntityManager entityManager;

	public List<NoteReply> findListByDate(Note note) {
		List<NoteReply> data = new ArrayList<NoteReply>();
		String jpql = "select noteReply from NoteReply noteReply where noteReply.note = :note ";
		data = entityManager.createQuery(jpql, NoteReply.class).setFlushMode(FlushModeType.COMMIT).setParameter("note", note).getResultList();
		return data;
	}

}
