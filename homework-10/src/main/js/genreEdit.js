import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import Menu from './menu';

class GenreEdit extends Component {

    genresPath = '/api/genres';
    
    genresRoute = '/genres';

    emptyGenre = {
        id: '',
        name: ''
    };

    constructor(props) {
        super(props);

        if (this.props.match.params.id && this.props.match.params.id.length > 0) {
            this.emptyGenre.id = this.props.match.params.id;
        }

        this.state = ({ genre: this.emptyGenre,
                        isLoading: true,
                        error: { status: false, message: '' } });
        this.handleInput = this.handleInput.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
    }

    componentDidMount() {
        const genre = this.state.genre;
        this.setState({ isLoading: true });

        if (genre.id && genre.id.length > 0) {
            this.setState({ isLoading: true });
            Promise.all([
                this.loadGenre(genre.id),
            ])
            .then(this.setState({ isLoading: false }));
        } else {
            this.setState({ isLoading: false });
        }
    }
    
    loadGenre(id) {
        fetch(this.genresPath + `/${id}`,
            { method: 'GET',
              header: { 'Accept': 'application/json' }
            })
            .then(response => {
                if (!response.ok) {
                    const error = response.json() || response.statusText;
                    return Promise.reject(error);
                }

                return response.json();
            })
            .then(data => { this.setState({ genre: data }); },
                  error => { this.setState({ error: { status: true, message: error.toString() } }); }
            );
    }

    handleInput(event) {
        const genre = this.state.genre;
        const fieldName = event.target.name;
        const fieldValue = event.target.value;

        genre[fieldName] = fieldValue;
        this.setState( { genre: genre });
    }

    handleSubmit(event) {
        event.preventDefault();
        const genre = this.state.genre;
        const path = genre.id ? this.genresPath + '/' + genre.id : this.genresPath;
        const method = genre.id ? 'PUT' : 'POST';

        fetch(path,
              { method: method,
                headers: { 'Accept': 'application/json',
                           'Content-type': 'application/json' },
                body: JSON.stringify(genre)
              })
              .then(response => { this.props.history.push(this.genresRoute); },
                    error => { Promise.reject(response.statusText); })
              .catch(error => {
                    this.setState({ error: { status: true, message: error.toString() } })
              })
    }

    handleCancel(event) {
        event.preventDefault();
        this.props.history.push(this.genresRoute);
    }

    render() {
        const genre = this.state.genre;
        const isLoading = this.state.isLoading;
        const error = this.state.error;

        if (isLoading) {
            return (
                <div>
                    <Menu />
                    <h2>Loading...</h2>
                </div>
            )
        }

        if (error.status) {
            return (
                <div>
                    <Menu />
                    <h2>Error: {error.message}</h2>
                </div>
            )
        }

        return (
            <div>
                <Menu />
                { genre.id ? <div className="header">Edit genre</div> : <div className="header">New genre</div> }
                <form>
                    <table className="tableEdit">
                        <tbody>
                        <tr>
                            <td>Name:</td>
                            <td><input type="text" name="name" value = {genre.name || ''} onChange={this.handleInput} /></td>
                        </tr>
                        <tr>
                            <td></td>
                            <td>
                                <button className="btnAction" onClick={this.handleSubmit}>Save</button>
                                <button className="btnAction" onClick={this.handleCancel}>Cancel</button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </form>
            </div>
        )
    }
}

export default GenreEdit;