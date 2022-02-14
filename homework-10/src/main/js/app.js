import React, { Component } from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Books from './booksList';
import BookEdit from './bookEdit';
import BookComments from './bookComments';
import Authors from './authorsList';
import AuthorEdit from './authorEdit';
import Genres from './genresList';
import GenreEdit from './genreEdit';
import CommentEdit from './commentEdit';

class App extends Component {
    render() {
        return (
            <Router>
                <Switch>
                    <Route path='/' exact={true} component={Books} />
                    <Route path='/books' exact={true} component={Books} />
                    <Route path='/books/new' exact={true} component={BookEdit} />
                    <Route path='/books/:id' exact={true} component={BookEdit} />
                    <Route path='/books/:bookId/comments' exact={true} component={BookComments} />
                    <Route path='/books/:bookId/comments/new' exact={true} component={CommentEdit} />
                    <Route path='/books/:bookId/comments/:id' component={CommentEdit} />
                    <Route path='/authors' exact={true} component={Authors} />
                    <Route path='/authors/new' exact={true} component={AuthorEdit} />
                    <Route path='/authors/:id' component={AuthorEdit} />
                    <Route path='/genres' exact={true} component={Genres} />
                    <Route path='/genres/new' exact={true} component={GenreEdit} />
                    <Route path='/genres/:id' component={GenreEdit} />
                </Switch>
            </Router>
        )
    }
}

export default App;