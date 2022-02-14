import React, { Component } from 'react';
import { Link } from 'react-router-dom';
import Menu from './menu';

class GenresList extends Component {

    genresPath = '/api/genres';

    genresRoute = '/genres';

    constructor(props) {
        super(props);
        this.state = { genres: [], isLoading: true, error: { status: false, message: '' } };
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

        fetch(this.genresPath,
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
                this.setState({ genres: data });
            })
            .catch(error => {
                this.setState({ error: { status: true, message: error.toString() } });
            });
    }

    remove(id) {
        fetch(this.genresPath + `/${id}`,
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

        const genresList = genres.map(genre => {
            return (
                <tr key={genre.id}>
                    <td>{genre.name}</td>
                    <td><button className="btnAction" onClick={() => this.openPath(this.genresRoute + '/' + genre.id)}>Edit</button>
                    <button className="btnAction" onClick={() => this.remove(genre.id)}>Delete</button></td>
                </tr>
            )
        });

        return (
            <div>
                <Menu />
                <div className="header">Genres</div>
                <div>
                    <button className="btn" onClick={() => this.openPath(this.genresRoute + '/new')}>New genre</button>
                    <table className="table">
                        <thead>
                        <tr>
                            <th>Name</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                            {genresList}
                        </tbody>
                    </table>
                </div>
            </div>
        );
    }
}
export default GenresList;