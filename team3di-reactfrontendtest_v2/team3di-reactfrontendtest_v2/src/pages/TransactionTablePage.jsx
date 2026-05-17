
import React, { useState }  from "react";
import axios from "axios";
import _ from "lodash"

const TransactionTablePage = () => {
    const [data, setData] = useState([]);
    const [formData, setFormData] = useState({})
    // Fix: correct destructuring – second element is the setter, first (unused) is the value
    const [, setError] = useState({})

    const getData = async (apiUrl, body, header) => {
        try {
            const resp = await axios.put(apiUrl, body, { header });
            setData(resp.data);
        } catch (err) {
            // Handle Error Here
            setError(err.response.data)
            alert(err.response.data)
        }
    };

    const handleSubmit = event => {
        event.preventDefault();

        // ES6 destructuring – includes new date range fields
        const { sortCode, accountNumber, startDate, endDate } = formData;

        if (_.isEmpty(sortCode) || _.isEmpty(accountNumber)) {
            setError({ message: 'Sort code and account number cannot be blank' })
            alert('Sort code and account number cannot be blank')
            return
        }

        const url = '/api/v1/alltransactions'

        // Task 2: include startDate and endDate in the payload.
        // When both are provided the backend filters transactions by initiationDate range.
        // When omitted (null) the backend returns all transactions (backward-compatible).
        const accountInput = {
            sortCode,
            accountNumber,
            startDate: startDate || null,
            endDate:   endDate   || null,
        }

        const headers = {
            'Content-Type': 'application/json',
            'Access-Control-Allow-Origin': '*',
            'Access-Control-Allow-Methods': 'PUT',
        };
        getData(url, accountInput, headers);
    }

    const handleChange = event => {
        const { name, value } = event.target
        setFormData({
            ...formData,       // Spread operator – preserve existing fields
            [name]: value      // ES6 computed property name
        })
    }

    return (
        <>
            <fieldset>
                <p>
                    Description: Enter Sort Code and Account Number to view account
                    balance and transactions. Optionally supply a date range to filter
                    transactions by initiation date.
                </p>
                <p>Demo payload: {JSON.stringify({ "sortCode": "53-68-92", "accountNumber": "73084635" })}</p>

                <p><strong>Task 1: Current balance is displayed below the form once account is loaded.</strong></p>
                <p><strong>Task 2: Use the Start Date / End Date fields to filter transactions by date range.</strong></p>

                <br />
                <li>Start Date and End Date are <strong>inclusive</strong> (e.g. 2019-04-01 to 2019-06-01 includes both boundary dates).</li>
                <li>Leave date fields empty to retrieve all transactions.</li>
            </fieldset>

            <form onSubmit={handleSubmit}>
                <fieldset>
                    {/* Sort code and account number inputs (existing) */}
                    <label>
                        <p>Sort Code:</p>
                        <input
                            type="text"
                            name="sortCode"
                            onChange={(e) => handleChange(e)}
                            value={formData.sortCode || ''}
                        />
                    </label>

                    <label>
                        <p>Account Number:</p>
                        <input
                            type="text"
                            name="accountNumber"
                            onChange={(e) => handleChange(e)}
                            value={formData.accountNumber || ''}
                        />
                    </label>

                    {/* Task 2: Date range inputs – sent to backend to filter by initiationDate */}
                    <label>
                        <p>Start Date (optional):</p>
                        <input
                            type="date"
                            name="startDate"
                            onChange={(e) => handleChange(e)}
                            value={formData.startDate || ''}
                        />
                    </label>

                    <label>
                        <p>End Date (optional):</p>
                        <input
                            type="date"
                            name="endDate"
                            onChange={(e) => handleChange(e)}
                            value={formData.endDate || ''}
                        />
                    </label>

                    <button type="submit">Submit</button>
                </fieldset>

                {!_.isEmpty(data) && (
                    <fieldset>
                        {/*
                          * Task 1: Display the current balance from account.currentBalance.
                          * toFixed(2) formats the number to two decimal places (e.g. 1071.78).
                          */}
                        <p>
                            <strong>
                                Current Balance: £{data.currentBalance != null
                                    ? data.currentBalance.toFixed(2)
                                    : 'N/A'}
                            </strong>
                        </p>

                        <table>
                            <tbody>
                                <tr>
                                    <th>Account Number</th>
                                    <th>Target Owner Name</th>
                                    <th>Amount (£)</th>
                                    <th>Initiation Date</th>
                                </tr>
                                {data?.transactions?.map(transaction => (
                                    // key suppresses React virtual-DOM warning and enables efficient re-renders
                                    <tr key={transaction.id}>
                                        <td>{data.accountNumber}</td>
                                        <td>{transaction.targetOwnerName}</td>
                                        <td>{transaction.amount}</td>
                                        <td>{transaction.initiationDate}</td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </fieldset>
                )}
            </form>
        </>
    );
}

export default TransactionTablePage;