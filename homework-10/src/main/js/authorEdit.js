import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom';
import Menu from './menu';

class AuthorEdit extends Component {

    authorsPath = '/api/authors';
    
    authorsRoute = '/authors';

    emptyAuthor = {
        id: '',
        surname: '',
        name: '',
        patronymic: ''
    };

    constructor(props) {
        super(props);

        if (this.props.match.params.id && this.props.match.params.id.length > 0) {
            this.emptyAuthor.id = this.props.match.params.id;
        }

        this.state = ({ author: this.emptyAuthor,
                        isLoading: true,
                        error: { status: false, message: '' } });
        this.handleInput = this.handleInput.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
    }

    componentDidMount() {
        const author = this.state.author;
        this.setState({ isLoading: true });

        if (author.id && author.id.length > 0) {
            this.setState({ isLoading: true });
            Promise.all([
                this.loadAuthor(author.id),
            ])
            .then(this.setState({ isLoading: false }));
        } else {
            this.setState({ isLoading: false });
        }
    }
    
    loadAuthor(id) {
        fetch(this.authorsPath + `/${id}`,
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
            .then(data => { this.setState({ author: data }); },
                  error => { this.setState({ error: { status: true, message: error.toString() } }); }
            );
    }

    handleInput(event) {
        const author = this.state.author;
        const fieldName = event.target.name;
        const fieldValue = event.target.value;

        author[fieldName] = fieldValue;
        this.setState( { author: author });
    }

    handleSubmit(event) {
        event.preventDefault();
        const author = this.state.author;
        const path = author.id ? this.authorsPath + '/' + author.id : this.authorsPath;
        const method = author.id ? 'PUT' : 'POST';

        fetch(path,
              { method: method,
                headers: { 'Accept': 'application/json',
                           'Content-type': 'application/json' },
                body: JSON.stringify(author)
              })
              .then(response => { this.props.history.push(this.authorsRoute); },
                    error => { Promise.reject(response.statusText); })
              .catch(error => {
                    this.setState({ error: { status: true, message: error.toString() } })
              })
    }

    handleCancel(event) {
        event.preventDefault();
        this.props.history.push(this.authorsRoute);
    }

    render() {
        const author = this.state.author;
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
                { author.id ? <div className="header">Edit author</div> : <div className="header">New author</div> }
                <form>
                    <table className="tableEdit">
                        <tbody>
                        <tr>
                            <td>Surname:</td>
                            <td><input type="text" name="surname" value = {author.surname || ''} onChange={this.handleInput} /></td>
                        </tr>
                        <tr>
                            <td>Name:</td>
                            <td><input type="text" name="name" value = {author.name || ''} onChange={this.handleInput} /></td>
                        </tr>
                        <tr>
                            <td>Patronymic:</td>
                            <td><input type="text" name="patronymic" value = {author.patronymic || ''} onChange={this.handleInput} /></td>
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

export default AuthorEdit;