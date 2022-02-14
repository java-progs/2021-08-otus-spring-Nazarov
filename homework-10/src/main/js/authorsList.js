import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import Menu from './menu';

class AuthorsList extends Component {

    authorsPath = '/api/authors';

    authorsRoute = '/authors';

    constructor(props) {
        super(props);
        this.state = { authors: [], isLoading: true, error: { status: false, message: '' } };
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

        fetch(this.authorsPath,
            { method: 'GET',
              headers: {
                'Accept': 'application/json'
              }
            })
            .then(response => {
                this.setState({ isLoading: false });

                if (!response.ok) {
                    const error = response.json() || response.statusText;
                    return Promise.reject(error);
                }

                return response.json();
            })
            .then(data => {
                this.setState({ authors: data });
            })
            .catch(error => {
                this.setState({ error: { status: true, message: error.toString() } });
            });
    }

    remove(id) {
        fetch(this.authorsPath + `/${id}`,
            { method: 'DELETE',
              headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
              }
            })
            .then(response =>  {
                if(!response.ok) {
                    const error = response.text();
                    return Promise.reject(error);
                }
            })
            .then(result => { this.load() },
                  error => { error.then(value => alert(value)) })
    }

    render() {
        const authors = this.state.authors;
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

        const authorsList = authors.map(author => {
            return (
                <tr key={author.id}>
                    <td>{author.surname}</td>
                    <td>{author.name}</td>
                    <td>{author.patronymic}</td>
                    <td><button className="btnAction" onClick={() => this.openPath(this.authorsRoute + '/' + author.id)}>Edit</button>
                    <button className="btnAction" onClick={() => this.remove(author.id)}>Delete</button></td>
                </tr>
            )
        });

        return (
            <div>
                <Menu />
                <div className="header">Authors</div>
                <div>
                    <button className="btn" onClick={() => this.openPath(this.authorsRoute + '/new')}>New author</button>
                    <table className="table">
                        <thead>
                        <tr>
                            <th>Surname</th>
                            <th>Name</th>
                            <th>Patronymic</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                            {authorsList}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}
export default AuthorsList;