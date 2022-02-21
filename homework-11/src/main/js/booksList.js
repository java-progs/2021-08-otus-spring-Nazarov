import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import Menu from './menu';

class BooksList extends Component {

    booksPath = '/api/books';
    authorsPath = '/api/authors';
    genresPath = '/api/genres';

    booksRoute = '/books';

    constructor(props) {
        super(props);
        this.state = { books: [], authors: [], genres: [], isLoading: true, error: { status: false, message: '' } };
        this.remove = this.remove.bind(this);
        this.load = this.load.bind(this);
        this.openPath = this.openPath.bind(this);
    }

    componentDidMount() {
        this.load();
    }

    openPath(path) {
        this.props.history.push(path);
    }

    load() {
        this.setState({ isLoading: true });
        this.setState({ error: { status: false, message: ' ' } });

        Promise.all([
            fetch(this.authorsPath, {method:'GET', headers:{'Accept':'application/json'}})
                .then(response => response.ok ? response.json() : Promise.reject())
                .then(data => this.setState({authors: data})),
            fetch(this.genresPath, {method:'GET', headers:{'Accept':'application/json'}})
                .then(response => response.ok ? response.json() : Promise.reject())
                .then(data => this.setState({genres: data})),
            fetch(this.booksPath, {method:'GET', headers:{'Accept':'application/json'}})
                .then(response => response.ok ? response.json() : Promise.reject())
                .then(data => this.setState({books: data}))
        ]).catch(error => this.setState({error:{status: true, message: error}}));

        this.setState({ isLoading: false });
    }

    remove(id) {
        fetch(this.booksPath + `/${id}`,
            { method: 'DELETE',
              headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
              }
            })
            .then(() => this.load());
    }

    render() {
        const books = this.state.books;
        const authors = this.state.authors;
        const genres = this.state.genres;
        const isLoading = this.state.isLoading;
        const error = this.state.error;

        if (isLoading) {
            return (
                <div>
                    <b>Loading...</b>
                </div>
            )
        }

        if(error.status) {
            return (
                <div>
                    <b>Error: {error.message}</b>
                </div>
            )
        }

        const booksList = books.map(book => {

            const bookAuthors = authors.filter(a => {
                return (book.authorsId.indexOf(a.id) > -1)
            });

            const authorsList = bookAuthors.map(a => {
                return (<p key={a.id}>{a.fullName}</p>)
            })

            const bookGenres = genres.filter(g => {
                return (book.genresId.indexOf(g.id) > -1)
            });

            const genresList = bookGenres.map(g => {
                return (<p key={g.id}>{g.name}</p>)
            });

            return (
                <tr key={book.id}>
                    <td>{book.name}</td>
                    <td>{book.isbn}</td>
                    <td>{authorsList}</td>
                    <td>{genresList}</td>
                    <td><button className="btnAction" onClick={() => this.openPath(this.booksRoute + '/' + book.id + '/comments')}>Comments</button>
                    <button className="btnAction" onClick={() => this.openPath(this.booksRoute + '/' + book.id)}>Edit</button>
                    <button className="btnAction" onClick={() => this.remove(book.id)}>Delete</button></td>
                </tr>
            )
        });

        return (
            <div>
                <Menu />
                <div className="header">Books</div>
                <div>
                    <button className="btn" onClick={() => this.openPath(this.booksRoute + '/new')}>New book</button>
                    <table className="table">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>ISBN</th>
                            <th>Authors</th>
                            <th>Genres</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                            {booksList}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}
export default BooksList;