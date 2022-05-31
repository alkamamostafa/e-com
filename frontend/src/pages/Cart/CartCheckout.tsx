import React, { FC, ReactElement } from "react";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faShoppingBag, faQrcode } from "@fortawesome/free-solid-svg-icons";

import { ORDER } from "../../constants/routeConstants";

type PropsType = {
    totalPrice: number;
    updatePriceCarts: () => void;
};

const CartCheckout: FC<PropsType> = ({ totalPrice, updatePriceCarts }): ReactElement => {
    return (
        <div className="row">

            <div className="col-3">
               
            </div>
            <div className="col-6">
                <input
                    type="text"
                    placeholder="coupon"
                    className="form-control input-number "

                />
            </div>
            <div className="col-2">
                <div className="form-row">
                    <button className="btn btn-warning mb-6" onClick={() => updatePriceCarts()} >
                        <FontAwesomeIcon className="mr-6" icon={faQrcode} /> Add Coupon
                    </button>
                </div>
                <br />
            </div>

            <div className="col-9">
                <p className="h5 text-right">
                    Total: $ <span>{totalPrice}</span>
                </p>
            </div>
            <div className="col-3">
                <div className="form-row">
                    <Link to={ORDER}>
                        <button className="btn btn-success">
                            <FontAwesomeIcon className="mr-2" icon={faShoppingBag} /> Checkout
                        </button>
                    </Link>
                </div>
            </div>




        </div>
    );
};

export default CartCheckout;
