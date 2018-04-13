import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 * 
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를  
 * 유지하는 데이터베이스이다. 
 */
public class MovieDB {
	private MyLinkedList<Genre> genreList;

    public MovieDB() {
    	genreList = new MyLinkedList<>();
    }

    public void insert(MovieDBItem item) {
    	Genre newGenre = new Genre(item.getGenre(), item.getTitle());

		for (Genre curr : genreList) {
			// if genre already exists
			if ( curr.equals(newGenre) ) {
				curr.getList().add(item.getTitle());
				return;
			}
		}
		// else insert new genre
		genreList.add(newGenre);
    }

    public void delete(MovieDBItem item) {
    	Genre ofItem = new Genre(item.getGenre());

    	for (Genre curr : genreList) {
    		// if genre exists
			if (curr.equals(ofItem)) {
				curr.getList().remove(item.getTitle());
				// check if genre became empty
				if (curr.getList().isEmpty())
					genreList.remove(curr);
				return;
			}
		}
    }

    public MyLinkedList<MovieDBItem> search(String term) {

        MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

        for (Genre genre : genreList) {
        	for (String title : genre.getList()) {
        		if (title.contains(term))
        			results.add(new MovieDBItem(genre.getItem(), title));
			}
		}

        return results;
    }
    
    public MyLinkedList<MovieDBItem> items() {
        MyLinkedList<MovieDBItem> results = new MyLinkedList<MovieDBItem>();

        for (Genre genre : genreList) {
        	for (String title : genre.getList()) {
        		results.add(new MovieDBItem(genre.getItem(), title));
			}
		}

    	return results;
    }
}

class Genre extends Node<String> implements Comparable<Genre> {

	private MovieList list;

	public Genre(String name) {
		super(name);
		list = new MovieList(this);
	}

	public Genre(String name, String title) {
		super(name);
		list = new MovieList(this, title);
	}

	public MovieList getList() {
		return list;
	}
	
	@Override
	public int compareTo(Genre o) {
		return this.getItem().compareTo(o.getItem());
	}

	@Override
	public int hashCode() {
		// modification from sample code in MovieDBItem.java
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getItem() == null) ? 0 : this.getItem().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// modification from sample code in MovieDBItem.java
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Genre other = (Genre) obj;
		if (this.getItem() == null) {
			if (other.getItem() != null)
				return false;
		} else if (!this.getItem().equals(other.getItem()))
			return false;
		return true;
	}
}

class MovieList extends MyLinkedList<String> implements ListInterface<String> {

	public MovieList() {
		super();
	}

	public MovieList(Genre genre) {
		head = genre;
		head.setNext(null);
		numItems = 0;
	}

	public MovieList(Genre genre, String title) {
		head = genre;
		head.setNext(new Node<> (title));
		numItems = 1;
	}

}
